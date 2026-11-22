package contacteraser.mobile.mypapit.net.contacteraser.ui.screens

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat
import contacteraser.mobile.mypapit.net.contacteraser.R
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ThemeMode
import contacteraser.mobile.mypapit.net.contacteraser.ui.state.ContactsUiState
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.AppLayout
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.AppSpacing
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.AppTouchTarget
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.DeleteContactsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    uiState: ContactsUiState,
    onThemeModeChange: (ThemeMode) -> Unit,
    onRequestPermissions: () -> Unit,
    onOpenAppSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val versionLabel = remember(context) { resolveVersionLabel(context) }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0),
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.titleLarge
                    )
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
            val useSegmentedButtons = maxWidth >= 340.dp && LocalDensity.current.fontScale < 1.35f

            Column(
                modifier = Modifier
                    .widthIn(max = AppLayout.maxContentWidth)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
            ) {
                // Appearance section
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
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
                                imageVector = Icons.Outlined.Palette,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.appearance_section_title),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.semantics { heading() }
                            )
                        }

                        Text(
                            text = stringResource(R.string.appearance_section_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val options = listOf(
                            Triple(ThemeMode.SYSTEM, R.string.theme_system, Icons.Outlined.SettingsBrightness),
                            Triple(ThemeMode.LIGHT, R.string.theme_light, Icons.Outlined.LightMode),
                            Triple(ThemeMode.DARK, R.string.theme_dark, Icons.Outlined.DarkMode)
                        )

                        if (useSegmentedButtons) {
                            SingleChoiceSegmentedButtonRow(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                options.forEachIndexed { index, (mode, labelRes, icon) ->
                                    SegmentedButton(
                                        selected = themeMode == mode,
                                        onClick = { onThemeModeChange(mode) },
                                        shape = SegmentedButtonDefaults.itemShape(
                                            index = index,
                                            count = options.size
                                        ),
                                        icon = {
                                            SegmentedButtonDefaults.Icon(active = themeMode == mode) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        },
                                        modifier = Modifier.heightIn(min = AppTouchTarget.minSize)
                                    ) {
                                        Text(
                                            text = stringResource(labelRes),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        } else {
                            Column(modifier = Modifier.selectableGroup()) {
                                options.forEach { (mode, labelRes, _) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = AppTouchTarget.minSize)
                                            .selectable(
                                                selected = themeMode == mode,
                                                role = Role.RadioButton,
                                                onClick = { onThemeModeChange(mode) }
                                            )
                                            .padding(horizontal = AppSpacing.xs),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
                                    ) {
                                        RadioButton(
                                            selected = themeMode == mode,
                                            onClick = null
                                        )
                                        Text(
                                            text = stringResource(labelRes),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Permissions section
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
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
                                imageVector = Icons.Outlined.AdminPanelSettings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.permissions_section_title),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.semantics { heading() }
                            )
                        }

                        Text(
                            text = stringResource(R.string.permissions_section_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        PermissionStatusRow(
                            label = stringResource(R.string.permission_read_label),
                            granted = uiState.hasReadPermission
                        )
                        PermissionStatusRow(
                            label = stringResource(R.string.permission_write_label),
                            granted = uiState.hasWritePermission
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm, Alignment.End)
                        ) {
                            if (!uiState.hasReadPermission || !uiState.hasWritePermission) {
                                OutlinedButton(
                                    onClick = onRequestPermissions,
                                    modifier = Modifier.heightIn(min = AppTouchTarget.minSize)
                                ) {
                                    Text(text = stringResource(R.string.grant_permission_action))
                                }
                            }
                            OutlinedButton(
                                onClick = onOpenAppSettings,
                                modifier = Modifier.heightIn(min = AppTouchTarget.minSize)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(AppSpacing.sm))
                                Text(text = stringResource(R.string.open_settings_action))
                            }
                        }
                    }
                }

                // About section
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
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
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(MaterialTheme.shapes.medium)
                            )
                            Column {
                                Text(
                                    text = stringResource(R.string.about_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.semantics { heading() }
                                )
                                Text(
                                    text = versionLabel,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Text(
                            text = stringResource(R.string.about_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                            Text(
                                text = stringResource(R.string.copyright),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.license_notice),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.sm))
            }
        }
    }
}

@Composable
private fun PermissionStatusRow(
    label: String,
    granted: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Surface(
            color = if (granted) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            },
            contentColor = if (granted) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onErrorContainer
            },
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = stringResource(
                    if (granted) R.string.permission_status_granted else R.string.permission_status_missing
                ),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = AppSpacing.sm, vertical = AppSpacing.xs)
            )
        }
    }
}

private fun resolveVersionLabel(context: Context): String {
    return try {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionCode = PackageInfoCompat.getLongVersionCode(info)
        context.getString(R.string.about_version_format, info.versionName ?: "", versionCode)
    } catch (_: PackageManager.NameNotFoundException) {
        context.getString(R.string.about_version_unknown)
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenLightPreview() {
    DeleteContactsTheme(themeMode = ThemeMode.LIGHT, reducedMotionOverride = true) {
        SettingsScreen(
            themeMode = ThemeMode.SYSTEM,
            uiState = ContactsUiState(hasReadPermission = true, hasWritePermission = false),
            onThemeModeChange = {},
            onRequestPermissions = {},
            onOpenAppSettings = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenDarkPreview() {
    DeleteContactsTheme(themeMode = ThemeMode.DARK, reducedMotionOverride = true) {
        SettingsScreen(
            themeMode = ThemeMode.DARK,
            uiState = ContactsUiState(hasReadPermission = true, hasWritePermission = true),
            onThemeModeChange = {},
            onRequestPermissions = {},
            onOpenAppSettings = {}
        )
    }
}
