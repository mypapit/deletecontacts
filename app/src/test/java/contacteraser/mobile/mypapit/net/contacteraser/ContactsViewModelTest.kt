package contacteraser.mobile.mypapit.net.contacteraser

import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactFilter
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactInfo
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactSummary
import contacteraser.mobile.mypapit.net.contacteraser.data.model.DeletionProgress
import contacteraser.mobile.mypapit.net.contacteraser.data.model.DeletionResult
import contacteraser.mobile.mypapit.net.contacteraser.data.repository.ContactDataSource
import contacteraser.mobile.mypapit.net.contacteraser.ui.state.ContactsViewModel
import contacteraser.mobile.mypapit.net.contacteraser.ui.state.DeleteRequest
import contacteraser.mobile.mypapit.net.contacteraser.ui.state.UiMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContactsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun grantingReadPermission_loadsSummaryAndWithoutPhoneContacts() = runTest {
        val dataSource = FakeContactDataSource(
            initialContacts = listOf(
                ContactInfo(1L, "Alice", "k1", hasPhoneNumber = false, primaryEmail = "alice@test.com"),
                ContactInfo(2L, "Bob", "k2", hasPhoneNumber = true, primaryPhone = "+15550101"),
                ContactInfo(3L, "Charlie", "k3", hasPhoneNumber = false)
            )
        )
        val viewModel = ContactsViewModel(dataSource)

        viewModel.onPermissionsUpdated(hasRead = true, hasWrite = false)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.hasReadPermission)
        assertFalse(state.hasWritePermission)
        assertEquals(ContactSummary(totalContacts = 3, contactsWithoutPhone = 2), state.summary)
        assertEquals(listOf("Alice", "Charlie"), state.contacts.map { it.displayName })
    }

    @Test
    fun switchingFilterAndSearching_filtersVisibleContacts() = runTest {
        val dataSource = FakeContactDataSource(
            initialContacts = listOf(
                ContactInfo(1L, "Alice Tan", "k1", hasPhoneNumber = false, primaryEmail = "alice@work.org"),
                ContactInfo(2L, "Bob Lim", "k2", hasPhoneNumber = true, primaryPhone = "+60123456789")
            )
        )
        val viewModel = ContactsViewModel(dataSource)
        viewModel.onPermissionsUpdated(hasRead = true, hasWrite = true)
        advanceUntilIdle()

        viewModel.setFilter(ContactFilter.ALL)
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.filteredContacts.size)

        viewModel.setSearchQuery("6012")
        assertEquals(listOf("Bob Lim"), viewModel.uiState.value.filteredContacts.map { it.displayName })

        viewModel.setSearchQuery("work.org")
        assertEquals(listOf("Alice Tan"), viewModel.uiState.value.filteredContacts.map { it.displayName })
    }

    @Test
    fun selectAndDeleteSelected_updatesStoreAndClearsSelection() = runTest {
        val dataSource = FakeContactDataSource(
            initialContacts = listOf(
                ContactInfo(1L, "Alice", "k1", hasPhoneNumber = false),
                ContactInfo(2L, "Bob", "k2", hasPhoneNumber = false),
                ContactInfo(3L, "Cara", "k3", hasPhoneNumber = true, primaryPhone = "+15559999")
            )
        )
        val viewModel = ContactsViewModel(dataSource)
        viewModel.onPermissionsUpdated(hasRead = true, hasWrite = true)
        advanceUntilIdle()

        viewModel.toggleSelection(1L)
        assertEquals(setOf(1L), viewModel.uiState.value.selectedIds)

        viewModel.requestDeleteSelected()
        val pending = viewModel.uiState.value.pendingConfirmation as DeleteRequest.DeleteSelected
        assertEquals(1, pending.contacts.size)

        viewModel.confirmPendingDeletion()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.pendingConfirmation)
        assertNull(state.deletionProgress)
        assertTrue(state.selectedIds.isEmpty())
        assertEquals(UiMessage.DeletedSelected(1), state.snackbarMessage)
        assertEquals(ContactSummary(totalContacts = 2, contactsWithoutPhone = 1), state.summary)
        assertEquals(listOf("Bob"), state.contacts.map { it.displayName })
    }

    @Test
    fun deleteWithoutPhone_keepsContactsWithNumbers() = runTest {
        val dataSource = FakeContactDataSource(
            initialContacts = listOf(
                ContactInfo(1L, "NoPhone1", "k1", hasPhoneNumber = false),
                ContactInfo(2L, "WithPhone", "k2", hasPhoneNumber = true, primaryPhone = "12345"),
                ContactInfo(3L, "NoPhone2", "k3", hasPhoneNumber = false)
            )
        )
        val viewModel = ContactsViewModel(dataSource)
        viewModel.onPermissionsUpdated(hasRead = true, hasWrite = true)
        advanceUntilIdle()

        viewModel.requestDeleteWithoutPhone()
        assertTrue(viewModel.uiState.value.pendingConfirmation is DeleteRequest.DeleteWithoutPhone)

        viewModel.confirmPendingDeletion()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(UiMessage.DeletedWithoutPhone(2), state.snackbarMessage)
        assertEquals(ContactSummary(totalContacts = 1, contactsWithoutPhone = 0), state.summary)
        assertTrue(state.contacts.isEmpty())
    }

    @Test
    fun deleteFailure_emitsDeleteFailedMessage() = runTest {
        val dataSource = FakeContactDataSource(
            initialContacts = listOf(ContactInfo(1L, "NoPhone1", "k1", hasPhoneNumber = false)),
            failOnDelete = true
        )
        val viewModel = ContactsViewModel(dataSource)
        viewModel.onPermissionsUpdated(hasRead = true, hasWrite = true)
        advanceUntilIdle()

        viewModel.requestDeleteAll()
        viewModel.confirmPendingDeletion()
        advanceUntilIdle()

        assertEquals(UiMessage.DeleteFailed, viewModel.uiState.value.snackbarMessage)
        assertNull(viewModel.uiState.value.deletionProgress)
    }

    private class FakeContactDataSource(
        initialContacts: List<ContactInfo>,
        private val failOnDelete: Boolean = false
    ) : ContactDataSource {
        private val contacts = initialContacts.toMutableList()

        override suspend fun getContactSummary(): ContactSummary {
            return ContactSummary(
                totalContacts = contacts.size,
                contactsWithoutPhone = contacts.count { !it.hasPhoneNumber }
            )
        }

        override suspend fun getContacts(filter: ContactFilter): List<ContactInfo> {
            return when (filter) {
                ContactFilter.WITHOUT_PHONE -> contacts.filter { !it.hasPhoneNumber }
                ContactFilter.ALL -> contacts.toList()
            }
        }

        override suspend fun deleteContacts(
            contacts: List<ContactInfo>,
            onProgress: (DeletionProgress) -> Unit
        ): DeletionResult {
            if (failOnDelete) {
                throw IllegalStateException("Simulated delete failure")
            }
            val idsToDelete = contacts.map { it.id }.toSet()
            contacts.forEachIndexed { index, contact ->
                onProgress(DeletionProgress(index + 1, contacts.size, contact.displayName))
            }
            val before = this.contacts.size
            this.contacts.removeAll { it.id in idsToDelete }
            val deleted = before - this.contacts.size
            return DeletionResult(deletedCount = deleted, requestedCount = contacts.size)
        }
    }
}
