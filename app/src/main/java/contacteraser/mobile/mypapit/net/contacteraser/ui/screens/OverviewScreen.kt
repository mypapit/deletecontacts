package contacteraser.mobile.mypapit.net.contacteraser.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ManageSearch
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.PhoneDisabled
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import contacteraser.mobile.mypapit.net.contacteraser.R
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactFilter
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactSummary
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ThemeMode
import contacteraser.mobile.mypapit.net.contacteraser.ui.components.AnimatedMetricValue
import contacteraser.mobile.mypapit.net.contacteraser.ui.components.PermissionBannerCard
import contacteraser.mobile.mypapit.net.contacteraser.ui.state.ContactsUiState
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.AppElevation
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.AppLayout
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.AppSpacing
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.AppTouchTarget
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.DeleteContactsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    uiState: ContactsUiState,
    onRefresh: () -> Unit,
    onRequestReadPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onInspectContacts: (ContactFilter) -> Unit,
    onDeleteWithoutPhone: () -> Unit,
    onDeleteAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0),
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
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
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            val isExpandedLayout = maxWidth >= 700.dp
            val sideBySideMetrics = maxWidth >= 340.dp && LocalDensity.current.fontScale < 1.35f

            Column(
                modifier = Modifier
                    .widthIn(max = if (isExpandedLayout) 1040.dp else AppLayout.maxContentWidth)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                    Text(
                        text = stringResource(R.string.main_heading),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.semantics { heading() }
                    )
                    Text(
                        text = stringResource(R.string.main_intro),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!uiState.hasReadPermission) {
                    PermissionBannerCard(
                        isPermanentlyDenied = uiState.permissionPermanentlyDenied,
                        onRequestPermission = onRequestReadPermission,
                        onOpenSettings = onOpenSettings
                    )
                }

                if (uiState.isLoadingSummary) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                if (uiState.summaryError && uiState.summary == null) {
                    SummaryErrorCard(onRetry = onRefresh)
                }

                if (isExpandedLayout) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.lg),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                        ) {
                            MetricsSection(
                                summary = uiState.summary,
                                enabled = uiState.hasReadPermission,
                                sideBySide = sideBySideMetrics,
                                onInspectContacts = onInspectContacts
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                        ) {
                            QuickCleanupCard(
                                uiState = uiState,
                                onInspectContacts = onInspectContacts,
                                onDeleteWithoutPhone = onDeleteWithoutPhone
                            )
                            DangerZoneCard(
                                uiState = uiState,
                                onDeleteAll = onDeleteAll
                            )
                        }
                    }
                } else {
                    MetricsSection(
                        summary = uiState.summary,
                        enabled = uiState.hasReadPermission,
                        sideBySide = sideBySideMetrics,
                        onInspectContacts = onInspectContacts
                    )
                    QuickCleanupCard(
                        uiState = uiState,
                        onInspectContacts = onInspectContacts,
                        onDeleteWithoutPhone = onDeleteWithoutPhone
                    )
                    DangerZoneCard(
                        uiState = uiState,
                        onDeleteAll = onDeleteAll
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.sm))
            }
        }
    }
}

@Composable
private fun MetricsSection(
    summary: ContactSummary?,
    enabled: Boolean,
    sideBySide: Boolean,
    onInspectContacts: (ContactFilter) -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val withoutPhoneContainer = MaterialTheme.colorScheme.tertiaryContainer
        .copy(alpha = 0.55f)
        .compositeOver(surfaceColor)

    if (sideBySide) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            MetricCard(
                title = stringResource(R.string.total_contacts_label),
                value = summary?.totalContacts,
                icon = Icons.Outlined.Contacts,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                contentColor = MaterialTheme.colorScheme.onSurface,
                iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                hintColor = MaterialTheme.colorScheme.primary,
                enabled = enabled,
                onClick = { onInspectContacts(ContactFilter.ALL) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            MetricCard(
                title = stringResource(R.string.without_phone_label),
                value = summary?.contactsWithoutPhone,
                icon = Icons.Outlined.PhoneDisabled,
                containerColor = withoutPhoneContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
                iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                iconTint = MaterialTheme.colorScheme.onTertiaryContainer,
                hintColor = MaterialTheme.colorScheme.onTertiaryContainer,
                enabled = enabled,
                onClick = { onInspectContacts(ContactFilter.WITHOUT_PHONE) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
            MetricCard(
                title = stringResource(R.string.total_contacts_label),
                value = summary?.totalContacts,
                icon = Icons.Outlined.Contacts,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                contentColor = MaterialTheme.colorScheme.onSurface,
                iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                hintColor = MaterialTheme.colorScheme.primary,
                enabled = enabled,
                onClick = { onInspectContacts(ContactFilter.ALL) },
                modifier = Modifier.fillMaxWidth()
            )
            MetricCard(
                title = stringResource(R.string.without_phone_label),
                value = summary?.contactsWithoutPhone,
                icon = Icons.Outlined.PhoneDisabled,
                containerColor = withoutPhoneContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
                iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                iconTint = MaterialTheme.colorScheme.onTertiaryContainer,
                hintColor = MaterialTheme.colorScheme.onTertiaryContainer,
                enabled = enabled,
                onClick = { onInspectContacts(ContactFilter.WITHOUT_PHONE) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: Int?,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    iconContainerColor: Color,
    iconTint: Color,
    hintColor: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = 132.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = AppElevation.level1,
            disabledElevation = AppElevation.level0
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppSpacing.lg),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconContainerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.md))

            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                AnimatedMetricValue(
                    value = value,
                    color = contentColor
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (enabled) {
                    Text(
                        text = stringResource(R.string.overview_card_tap_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = hintColor
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickCleanupCard(
    uiState: ContactsUiState,
    onInspectContacts: (ContactFilter) -> Unit,
    onDeleteWithoutPhone: () -> Unit
) {
    val withoutPhoneCount = uiState.summary?.contactsWithoutPhone ?: 0
    val canDeleteWithoutPhone = uiState.hasReadPermission && withoutPhoneCount > 0 && uiState.deletionProgress == null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                Text(
                    text = stringResource(R.string.quick_cleanup_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.semantics { heading() }
                )
                Text(
                    text = stringResource(R.string.quick_cleanup_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { onInspectContacts(ContactFilter.WITHOUT_PHONE) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = AppTouchTarget.minSize)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ManageSearch,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(AppSpacing.sm))
                Text(text = stringResource(R.string.review_no_phone_action))
            }

            FilledTonalButton(
                onClick = onDeleteWithoutPhone,
                enabled = canDeleteWithoutPhone,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = AppTouchTarget.minSize)
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteSweep,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(AppSpacing.sm))
                Text(text = stringResource(R.string.delete_no_phone))
            }
        }
    }
}

@Composable
private fun DangerZoneCard(
    uiState: ContactsUiState,
    onDeleteAll: () -> Unit
) {
    val totalCount = uiState.summary?.totalContacts ?: 0
    val canDeleteAll = uiState.hasReadPermission && totalCount > 0 && uiState.deletionProgress == null

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.45f)),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.22f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = stringResource(R.string.danger_zone_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.semantics { heading() }
                )
            }

            Text(
                text = stringResource(R.string.danger_zone_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = onDeleteAll,
                enabled = canDeleteAll,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = AppTouchTarget.minSize)
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteSweep,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(AppSpacing.sm))
                Text(text = stringResource(R.string.delete_all_contacts))
            }
        }
    }
}

@Composable
private fun SummaryErrorCard(onRetry: () -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = stringResource(R.string.contact_load_failed),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.heightIn(min = AppTouchTarget.minSize)
            ) {
                Text(text = stringResource(R.string.retry_action))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OverviewScreenLightPreview() {
    DeleteContactsTheme(themeMode = ThemeMode.LIGHT, reducedMotionOverride = true) {
        OverviewScreen(
            uiState = ContactsUiState(
                hasReadPermission = true,
                hasWritePermission = true,
                summary = ContactSummary(totalContacts = 148, contactsWithoutPhone = 19)
            ),
            onRefresh = {},
            onRequestReadPermission = {},
            onOpenSettings = {},
            onInspectContacts = {},
            onDeleteWithoutPhone = {},
            onDeleteAll = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OverviewScreenDarkPreview() {
    DeleteContactsTheme(themeMode = ThemeMode.DARK, reducedMotionOverride = true) {
        OverviewScreen(
            uiState = ContactsUiState(
                hasReadPermission = false,
                permissionPermanentlyDenied = false
            ),
            onRefresh = {},
            onRequestReadPermission = {},
            onOpenSettings = {},
            onInspectContacts = {},
            onDeleteWithoutPhone = {},
            onDeleteAll = {}
        )
    }
}

@Preview(showBackground = true, fontScale = 1.5f)
@Composable
private fun OverviewScreenLargeFontPreview() {
    DeleteContactsTheme(themeMode = ThemeMode.LIGHT, reducedMotionOverride = true) {
        OverviewScreen(
            uiState = ContactsUiState(
                hasReadPermission = true,
                summary = ContactSummary(totalContacts = 84, contactsWithoutPhone = 7)
            ),
            onRefresh = {},
            onRequestReadPermission = {},
            onOpenSettings = {},
            onInspectContacts = {},
            onDeleteWithoutPhone = {},
            onDeleteAll = {}
        )
    }
}
