package contacteraser.mobile.mypapit.net.contacteraser.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Deselect
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import contacteraser.mobile.mypapit.net.contacteraser.R
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactFilter
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactInfo
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactSummary
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ThemeMode
import contacteraser.mobile.mypapit.net.contacteraser.ui.components.ExpressiveLoadingIndicator
import contacteraser.mobile.mypapit.net.contacteraser.ui.components.PermissionBannerCard
import contacteraser.mobile.mypapit.net.contacteraser.ui.state.ContactsUiState
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.AppElevation
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.AppLayout
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.AppSpacing
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.AppTouchTarget
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.DeleteContactsTheme
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.avatarColorForIndex
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ContactsScreen(
    uiState: ContactsUiState,
    onRefresh: () -> Unit,
    onRequestReadPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onFilterChange: (ContactFilter) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleSelection: (Long) -> Unit,
    onSelectAllVisible: () -> Unit,
    onClearSelection: () -> Unit,
    onToggleExpanded: (Long) -> Unit,
    onDeleteSelected: () -> Unit,
    onDeleteWithoutPhone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inSelectionMode = uiState.selectedIds.isNotEmpty()
    val filteredContacts = uiState.filteredContacts

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AnimatedContent(
                targetState = inSelectionMode,
                label = "contacts_top_bar"
            ) { selecting ->
                if (selecting) {
                    TopAppBar(
                        windowInsets = WindowInsets(0),
                        navigationIcon = {
                            IconButton(onClick = onClearSelection) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = stringResource(R.string.close_selection_cd)
                                )
                            }
                        },
                        title = {
                            Text(
                                text = pluralStringResource(
                                    R.plurals.selection_count_title,
                                    uiState.selectedIds.size,
                                    uiState.selectedIds.size
                                ),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        actions = {
                            IconButton(onClick = onSelectAllVisible) {
                                Icon(
                                    imageVector = Icons.Outlined.SelectAll,
                                    contentDescription = stringResource(R.string.select_all_cd)
                                )
                            }
                            IconButton(onClick = onClearSelection) {
                                Icon(
                                    imageVector = Icons.Outlined.Deselect,
                                    contentDescription = stringResource(R.string.clear_selection_cd)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                } else {
                    TopAppBar(
                        windowInsets = WindowInsets(0),
                        title = {
                            Text(
                                text = stringResource(R.string.contacts_title),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        actions = {
                            if (uiState.hasReadPermission) {
                                IconButton(onClick = onRefresh) {
                                    Icon(
                                        imageVector = Icons.Outlined.Refresh,
                                        contentDescription = stringResource(R.string.refresh_action)
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            }
        },
        bottomBar = {
            val showDeleteSelected = inSelectionMode && uiState.hasReadPermission
            val showDeleteAllWithoutPhone = !inSelectionMode &&
                uiState.hasReadPermission &&
                uiState.filter == ContactFilter.WITHOUT_PHONE &&
                uiState.contacts.isNotEmpty()

            AnimatedVisibility(
                visible = showDeleteSelected || showDeleteAllWithoutPhone,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    tonalElevation = AppElevation.level2,
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = if (showDeleteSelected) onDeleteSelected else onDeleteWithoutPhone,
                            enabled = uiState.deletionProgress == null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            modifier = Modifier
                                .widthIn(max = AppLayout.maxContentWidth)
                                .fillMaxWidth()
                                .heightIn(min = AppTouchTarget.minSize)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteSweep,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.sm))
                            Text(
                                text = if (showDeleteSelected) {
                                    stringResource(R.string.delete_selected_action, uiState.selectedIds.size)
                                } else {
                                    stringResource(
                                        R.string.delete_all_without_phone_action,
                                        uiState.contacts.size
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = AppLayout.maxContentWidth)
                    .fillMaxSize()
            ) {
                if (!uiState.hasReadPermission) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(AppSpacing.lg),
                        contentAlignment = Alignment.Center
                    ) {
                        PermissionBannerCard(
                            isPermanentlyDenied = uiState.permissionPermanentlyDenied,
                            onRequestPermission = onRequestReadPermission,
                            onOpenSettings = onOpenSettings
                        )
                    }
                    return@Column
                }

                // Filter chips & Search bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                    ) {
                        val withoutPhoneLabel = uiState.summary?.let {
                            stringResource(R.string.filter_without_phone_count, it.contactsWithoutPhone)
                        } ?: stringResource(R.string.filter_without_phone)

                        val allContactsLabel = uiState.summary?.let {
                            stringResource(R.string.filter_all_contacts_count, it.totalContacts)
                        } ?: stringResource(R.string.filter_all_contacts)

                        FilterChip(
                            selected = uiState.filter == ContactFilter.WITHOUT_PHONE,
                            onClick = { onFilterChange(ContactFilter.WITHOUT_PHONE) },
                            label = { Text(text = withoutPhoneLabel) }
                        )
                        FilterChip(
                            selected = uiState.filter == ContactFilter.ALL,
                            onClick = { onFilterChange(ContactFilter.ALL) },
                            label = { Text(text = allContactsLabel) }
                        )
                    }

                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text(text = stringResource(R.string.search_contacts_placeholder)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = stringResource(R.string.clear_search_cd)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                when {
                    uiState.isLoadingContacts && uiState.contacts.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(AppSpacing.xl),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
                            ) {
                                ExpressiveLoadingIndicator()
                                Text(
                                    text = stringResource(R.string.loading_contacts),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    uiState.contactsError && uiState.contacts.isEmpty() -> {
                        StatePlaceholder(
                            icon = Icons.Outlined.ErrorOutline,
                            title = stringResource(R.string.contact_load_failed),
                            message = stringResource(R.string.contact_load_failed),
                            actionLabel = stringResource(R.string.retry_action),
                            onAction = onRefresh
                        )
                    }

                    filteredContacts.isEmpty() -> {
                        if (uiState.searchQuery.isNotBlank()) {
                            StatePlaceholder(
                                icon = Icons.Outlined.PersonSearch,
                                title = stringResource(R.string.no_search_results_title),
                                message = stringResource(R.string.no_search_results_message),
                                actionLabel = stringResource(R.string.clear_search_cd),
                                onAction = { onSearchQueryChange("") }
                            )
                        } else if (uiState.filter == ContactFilter.WITHOUT_PHONE) {
                            StatePlaceholder(
                                icon = Icons.Outlined.CheckCircle,
                                title = stringResource(R.string.no_contacts_without_phone_title),
                                message = stringResource(R.string.no_contacts_without_phone)
                            )
                        } else {
                            StatePlaceholder(
                                icon = Icons.Outlined.PersonSearch,
                                title = stringResource(R.string.no_contacts_all_title),
                                message = stringResource(R.string.no_contacts_all)
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = AppSpacing.lg,
                                end = AppSpacing.lg,
                                top = AppSpacing.sm,
                                bottom = AppSpacing.xl
                            ),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                        ) {
                            itemsIndexed(
                                items = filteredContacts,
                                key = { _, contact -> contact.id }
                            ) { index, contact ->
                                ContactListCard(
                                    contact = contact,
                                    index = index,
                                    selected = contact.id in uiState.selectedIds,
                                    expanded = contact.id in uiState.expandedContactIds,
                                    inSelectionMode = inSelectionMode,
                                    onCardClick = {
                                        if (inSelectionMode) {
                                            onToggleSelection(contact.id)
                                        } else {
                                            onToggleExpanded(contact.id)
                                        }
                                    },
                                    onCardLongClick = { onToggleSelection(contact.id) },
                                    onToggleSelection = { onToggleSelection(contact.id) },
                                    onToggleExpanded = { onToggleExpanded(contact.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContactListCard(
    contact: ContactInfo,
    index: Int,
    selected: Boolean,
    expanded: Boolean,
    inSelectionMode: Boolean,
    onCardClick: () -> Unit,
    onCardLongClick: () -> Unit,
    onToggleSelection: () -> Unit,
    onToggleExpanded: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        } else {
            MaterialTheme.colorScheme.surfaceContainerLowest
        },
        label = "contact_card_container"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outlineVariant
        },
        label = "contact_card_border"
    )
    val avatarColors = avatarColorForIndex(index, MaterialTheme.colorScheme)
    val initial = contact.displayName.trim().firstOrNull()?.uppercase(Locale.getDefault()) ?: "?"
    val subtitleText = if (contact.hasPhoneNumber) {
        contact.primaryPhone ?: stringResource(R.string.filter_all_contacts)
    } else {
        stringResource(R.string.contact_phone_none)
    }
    val selectContactDesc = stringResource(R.string.select_contact_cd, contact.displayName)

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .combinedClickable(
                onClick = onCardClick,
                onLongClick = onCardLongClick
            ),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, borderColor),
        colors = CardDefaults.outlinedCardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 68.dp)
                    .padding(start = AppSpacing.lg, end = AppSpacing.xs, top = AppSpacing.sm, bottom = AppSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(avatarColors.container),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.titleMedium,
                        color = avatarColors.content
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = contact.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitleText,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (contact.hasPhoneNumber) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.tertiary
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = onToggleExpanded) {
                    Icon(
                        imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                        contentDescription = stringResource(
                            if (expanded) R.string.collapse_contact_details_cd else R.string.expand_contact_details_cd
                        )
                    )
                }

                Checkbox(
                    checked = selected,
                    onCheckedChange = { onToggleSelection() },
                    modifier = Modifier
                        .size(AppTouchTarget.minSize)
                        .semantics { contentDescription = selectContactDesc }
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = AppSpacing.lg,
                            end = AppSpacing.lg,
                            bottom = AppSpacing.md
                        ),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.size(AppSpacing.xs))
                    DetailRow(
                        label = stringResource(R.string.contact_detail_id_label),
                        value = contact.id.toString()
                    )
                    DetailRow(
                        label = stringResource(R.string.contact_detail_lookup_label),
                        value = contact.lookupKey
                    )
                    DetailRow(
                        label = stringResource(R.string.contact_detail_phone_label),
                        value = subtitleText
                    )
                    if (!contact.primaryEmail.isNullOrBlank()) {
                        DetailRow(
                            label = stringResource(R.string.contact_detail_email_label),
                            value = contact.primaryEmail
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatePlaceholder(
    icon: ImageVector,
    title: String,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.xl),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (actionLabel != null && onAction != null) {
                OutlinedButton(
                    onClick = onAction,
                    modifier = Modifier.heightIn(min = AppTouchTarget.minSize)
                ) {
                    Text(text = actionLabel)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactsScreenPopulatedLightPreview() {
    DeleteContactsTheme(themeMode = ThemeMode.LIGHT, reducedMotionOverride = true) {
        ContactsScreen(
            uiState = ContactsUiState(
                hasReadPermission = true,
                summary = ContactSummary(12, 2),
                contacts = listOf(
                    ContactInfo(101L, "Alice Tan", "123r-abc", false, primaryEmail = "alice@example.com"),
                    ContactInfo(102L, "Bob Lim", "124r-def", false)
                ),
                selectedIds = setOf(101L),
                expandedContactIds = setOf(101L)
            ),
            onRefresh = {},
            onRequestReadPermission = {},
            onOpenSettings = {},
            onFilterChange = {},
            onSearchQueryChange = {},
            onToggleSelection = {},
            onSelectAllVisible = {},
            onClearSelection = {},
            onToggleExpanded = {},
            onDeleteSelected = {},
            onDeleteWithoutPhone = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactsScreenEmptyDarkPreview() {
    DeleteContactsTheme(themeMode = ThemeMode.DARK, reducedMotionOverride = true) {
        ContactsScreen(
            uiState = ContactsUiState(
                hasReadPermission = true,
                summary = ContactSummary(25, 0),
                contacts = emptyList()
            ),
            onRefresh = {},
            onRequestReadPermission = {},
            onOpenSettings = {},
            onFilterChange = {},
            onSearchQueryChange = {},
            onToggleSelection = {},
            onSelectAllVisible = {},
            onClearSelection = {},
            onToggleExpanded = {},
            onDeleteSelected = {},
            onDeleteWithoutPhone = {}
        )
    }
}
