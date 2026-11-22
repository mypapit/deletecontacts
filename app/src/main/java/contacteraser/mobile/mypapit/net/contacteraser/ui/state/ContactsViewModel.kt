package contacteraser.mobile.mypapit.net.contacteraser.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactFilter
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactInfo
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactSummary
import contacteraser.mobile.mypapit.net.contacteraser.data.model.DeletionProgress
import contacteraser.mobile.mypapit.net.contacteraser.data.repository.ContactDataSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DeleteRequest {
    data class DeleteSelected(val contacts: List<ContactInfo>) : DeleteRequest
    data class DeleteWithoutPhone(val count: Int) : DeleteRequest
    data class DeleteAll(val count: Int) : DeleteRequest
}

sealed interface UiMessage {
    data class DeletedSelected(val count: Int) : UiMessage
    data class DeletedWithoutPhone(val count: Int) : UiMessage
    data class DeletedAll(val count: Int) : UiMessage
    data object DeleteFailed : UiMessage
    data object PermissionRequired : UiMessage
}

data class ContactsUiState(
    val hasReadPermission: Boolean = false,
    val hasWritePermission: Boolean = false,
    val permissionPermanentlyDenied: Boolean = false,
    val summary: ContactSummary? = null,
    val isLoadingSummary: Boolean = false,
    val summaryError: Boolean = false,
    val filter: ContactFilter = ContactFilter.WITHOUT_PHONE,
    val searchQuery: String = "",
    val contacts: List<ContactInfo> = emptyList(),
    val selectedIds: Set<Long> = emptySet(),
    val expandedContactIds: Set<Long> = emptySet(),
    val isLoadingContacts: Boolean = false,
    val contactsError: Boolean = false,
    val pendingConfirmation: DeleteRequest? = null,
    val deletionProgress: DeletionProgress? = null,
    val snackbarMessage: UiMessage? = null
) {
    val filteredContacts: List<ContactInfo>
        get() {
            val query = searchQuery.trim()
            if (query.isEmpty()) return contacts
            return contacts.filter { contact ->
                contact.displayName.contains(query, ignoreCase = true) ||
                    (contact.primaryPhone?.contains(query, ignoreCase = true) == true) ||
                    (contact.primaryEmail?.contains(query, ignoreCase = true) == true)
            }
        }
}

/**
 * Coordinates permissions, dashboard metrics, contact browsing/selection, and confirmed deletions.
 */
class ContactsViewModel(
    private val contactDataSource: ContactDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    private var loadSummaryJob: Job? = null
    private var loadContactsJob: Job? = null

    fun onPermissionsUpdated(
        hasRead: Boolean,
        hasWrite: Boolean,
        permanentlyDenied: Boolean = false
    ) {
        val previousRead = _uiState.value.hasReadPermission
        _uiState.update {
            it.copy(
                hasReadPermission = hasRead,
                hasWritePermission = hasWrite,
                permissionPermanentlyDenied = permanentlyDenied && !hasRead
            )
        }
        if (hasRead && !previousRead) {
            refreshAll()
        } else if (!hasRead) {
            loadSummaryJob?.cancel()
            loadContactsJob?.cancel()
            _uiState.update {
                it.copy(
                    summary = null,
                    contacts = emptyList(),
                    selectedIds = emptySet(),
                    expandedContactIds = emptySet(),
                    isLoadingSummary = false,
                    isLoadingContacts = false,
                    summaryError = false,
                    contactsError = false
                )
            }
        }
    }

    fun refreshAll() {
        if (!_uiState.value.hasReadPermission) return
        loadSummary()
        loadContacts(_uiState.value.filter)
    }

    fun setFilter(filter: ContactFilter) {
        val changed = _uiState.value.filter != filter
        _uiState.update {
            it.copy(
                filter = filter,
                selectedIds = if (changed) emptySet() else it.selectedIds,
                expandedContactIds = if (changed) emptySet() else it.expandedContactIds
            )
        }
        if (_uiState.value.hasReadPermission && (changed || _uiState.value.contacts.isEmpty())) {
            loadContacts(filter)
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleSelection(contactId: Long) {
        _uiState.update { state ->
            val updated = if (contactId in state.selectedIds) {
                state.selectedIds - contactId
            } else {
                state.selectedIds + contactId
            }
            state.copy(selectedIds = updated)
        }
    }

    fun selectAllVisible() {
        _uiState.update { state ->
            val visibleIds = state.filteredContacts.map { it.id }.toSet()
            state.copy(selectedIds = state.selectedIds + visibleIds)
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedIds = emptySet()) }
    }

    fun toggleExpanded(contactId: Long) {
        _uiState.update { state ->
            val updated = if (contactId in state.expandedContactIds) {
                state.expandedContactIds - contactId
            } else {
                state.expandedContactIds + contactId
            }
            state.copy(expandedContactIds = updated)
        }
    }

    fun requestDeleteSelected() {
        val state = _uiState.value
        val selectedContacts = state.contacts.filter { it.id in state.selectedIds }
        if (selectedContacts.isEmpty()) return
        _uiState.update {
            it.copy(pendingConfirmation = DeleteRequest.DeleteSelected(selectedContacts))
        }
    }

    fun requestDeleteWithoutPhone() {
        val count = _uiState.value.summary?.contactsWithoutPhone
            ?: if (_uiState.value.filter == ContactFilter.WITHOUT_PHONE) _uiState.value.contacts.size else 0
        if (count <= 0) return
        _uiState.update {
            it.copy(pendingConfirmation = DeleteRequest.DeleteWithoutPhone(count))
        }
    }

    fun requestDeleteAll() {
        val count = _uiState.value.summary?.totalContacts ?: _uiState.value.contacts.size
        if (count <= 0) return
        _uiState.update {
            it.copy(pendingConfirmation = DeleteRequest.DeleteAll(count))
        }
    }

    fun dismissDeleteConfirmation() {
        _uiState.update { it.copy(pendingConfirmation = null) }
    }

    fun showPermissionRequiredMessage() {
        _uiState.update { it.copy(snackbarMessage = UiMessage.PermissionRequired) }
    }

    fun consumeSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun confirmPendingDeletion() {
        val request = _uiState.value.pendingConfirmation ?: return
        if (_uiState.value.deletionProgress != null) return

        _uiState.update {
            it.copy(
                pendingConfirmation = null,
                deletionProgress = DeletionProgress(
                    processed = 0,
                    total = when (request) {
                        is DeleteRequest.DeleteSelected -> request.contacts.size
                        is DeleteRequest.DeleteWithoutPhone -> request.count
                        is DeleteRequest.DeleteAll -> request.count
                    },
                    currentName = ""
                )
            )
        }

        viewModelScope.launch {
            try {
                // Re-query authoritative lists for bulk operations to avoid stale counts.
                val targets = when (request) {
                    is DeleteRequest.DeleteSelected -> request.contacts
                    is DeleteRequest.DeleteWithoutPhone ->
                        contactDataSource.getContacts(ContactFilter.WITHOUT_PHONE)
                    is DeleteRequest.DeleteAll ->
                        contactDataSource.getContacts(ContactFilter.ALL)
                }

                if (targets.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            deletionProgress = null,
                            selectedIds = emptySet(),
                            snackbarMessage = when (request) {
                                is DeleteRequest.DeleteSelected -> UiMessage.DeletedSelected(0)
                                is DeleteRequest.DeleteWithoutPhone -> UiMessage.DeletedWithoutPhone(0)
                                is DeleteRequest.DeleteAll -> UiMessage.DeletedAll(0)
                            }
                        )
                    }
                    refreshAll()
                    return@launch
                }

                val result = contactDataSource.deleteContacts(targets) { progress ->
                    _uiState.update { it.copy(deletionProgress = progress) }
                }

                val message = when (request) {
                    is DeleteRequest.DeleteSelected -> UiMessage.DeletedSelected(result.deletedCount)
                    is DeleteRequest.DeleteWithoutPhone -> UiMessage.DeletedWithoutPhone(result.deletedCount)
                    is DeleteRequest.DeleteAll -> UiMessage.DeletedAll(result.deletedCount)
                }

                _uiState.update {
                    it.copy(
                        deletionProgress = null,
                        selectedIds = emptySet(),
                        expandedContactIds = emptySet(),
                        snackbarMessage = message
                    )
                }
                refreshAll()
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        deletionProgress = null,
                        snackbarMessage = UiMessage.DeleteFailed
                    )
                }
            }
        }
    }

    private fun loadSummary() {
        loadSummaryJob?.cancel()
        loadSummaryJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSummary = true, summaryError = false) }
            try {
                val summary = contactDataSource.getContactSummary()
                _uiState.update {
                    it.copy(
                        summary = summary,
                        isLoadingSummary = false,
                        summaryError = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingSummary = false,
                        summaryError = true
                    )
                }
            }
        }
    }

    private fun loadContacts(filter: ContactFilter) {
        loadContactsJob?.cancel()
        loadContactsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingContacts = true, contactsError = false) }
            try {
                val list = contactDataSource.getContacts(filter)
                val validIds = list.map { it.id }.toSet()
                _uiState.update { state ->
                    state.copy(
                        contacts = list,
                        selectedIds = state.selectedIds.intersect(validIds),
                        expandedContactIds = state.expandedContactIds.intersect(validIds),
                        isLoadingContacts = false,
                        contactsError = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingContacts = false,
                        contactsError = true
                    )
                }
            }
        }
    }

    class Factory(
        private val contactDataSource: ContactDataSource
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
                return ContactsViewModel(contactDataSource) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
