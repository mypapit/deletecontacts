package contacteraser.mobile.mypapit.net.contacteraser.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import contacteraser.mobile.mypapit.net.contacteraser.R
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactInfo
import contacteraser.mobile.mypapit.net.contacteraser.data.model.DeletionProgress
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ThemeMode
import contacteraser.mobile.mypapit.net.contacteraser.ui.state.DeleteRequest
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.AppSpacing
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.AppTouchTarget
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.DeleteContactsTheme
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.LocalReducedMotion

@Composable
fun AnimatedMetricValue(
    value: Int?,
    color: Color,
    modifier: Modifier = Modifier
) {
    val reducedMotion = LocalReducedMotion.current
    val animatedCount by animateIntAsState(
        targetValue = value ?: 0,
        animationSpec = if (reducedMotion) {
            snap()
        } else {
            tween(durationMillis = 650, easing = FastOutSlowInEasing)
        },
        label = "metric_count"
    )
    val displayText = if (value == null) {
        stringResource(R.string.dashboard_count_placeholder)
    } else {
        animatedCount.toString()
    }
    Text(
        text = displayText,
        style = MaterialTheme.typography.displaySmall,
        color = color,
        maxLines = 1,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveLoadingIndicator(
    modifier: Modifier = Modifier
) {
    val reducedMotion = LocalReducedMotion.current
    if (reducedMotion) {
        CircularProgressIndicator(modifier = modifier.size(40.dp))
    } else {
        LoadingIndicator(modifier = modifier.size(48.dp))
    }
}

@Composable
fun PermissionBannerCard(
    isPermanentlyDenied: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
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
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.permission_title),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.semantics { heading() }
                    )
                }
            }

            Text(
                text = stringResource(
                    if (isPermanentlyDenied) {
                        R.string.permission_permanently_denied
                    } else {
                        R.string.permission_rationale_read
                    }
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (isPermanentlyDenied) {
                    Button(
                        onClick = onOpenSettings,
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
                } else {
                    Button(
                        onClick = onRequestPermission,
                        modifier = Modifier.heightIn(min = AppTouchTarget.minSize)
                    ) {
                        Text(text = stringResource(R.string.grant_permission_action))
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    request: DeleteRequest,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var acknowledgedDeleteAll by rememberSaveable(request) { mutableStateOf(false) }

    val titleText = when (request) {
        is DeleteRequest.DeleteSelected -> stringResource(R.string.confirm_delete_selected_title)
        is DeleteRequest.DeleteWithoutPhone -> stringResource(R.string.confirm_delete_no_phone_title)
        is DeleteRequest.DeleteAll -> stringResource(R.string.confirm_delete_all_title)
    }

    val messageText = when (request) {
        is DeleteRequest.DeleteSelected -> pluralStringResource(
            R.plurals.confirm_delete_selected_message,
            request.contacts.size,
            request.contacts.size
        )
        is DeleteRequest.DeleteWithoutPhone -> pluralStringResource(
            R.plurals.confirm_delete_no_phone_message,
            request.count,
            request.count
        )
        is DeleteRequest.DeleteAll -> pluralStringResource(
            R.plurals.confirm_delete_all_message,
            request.count,
            request.count
        )
    }

    val confirmLabel = when (request) {
        is DeleteRequest.DeleteAll -> stringResource(R.string.delete_all)
        is DeleteRequest.DeleteSelected,
        is DeleteRequest.DeleteWithoutPhone -> stringResource(R.string.delete)
    }

    val canConfirm = request !is DeleteRequest.DeleteAll || acknowledgedDeleteAll

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteForever,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        title = {
            Text(
                text = titleText,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WarningAmber,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.irreversible_warning_badge),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Text(
                    text = messageText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (request is DeleteRequest.DeleteAll) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = AppTouchTarget.minSize)
                                .toggleable(
                                    value = acknowledgedDeleteAll,
                                    role = Role.Checkbox,
                                    onValueChange = { acknowledgedDeleteAll = it }
                                )
                                .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
                        ) {
                            Checkbox(
                                checked = acknowledgedDeleteAll,
                                onCheckedChange = null
                            )
                            Text(
                                text = pluralStringResource(
                                    R.plurals.delete_all_checkbox_label,
                                    request.count,
                                    request.count
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = canConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier.heightIn(min = AppTouchTarget.minSize)
            ) {
                Text(text = confirmLabel)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.heightIn(min = AppTouchTarget.minSize)
            ) {
                Text(text = stringResource(R.string.cancel_action))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DeletionProgressDialog(
    progress: DeletionProgress
) {
    val reducedMotion = LocalReducedMotion.current
    val fraction = if (progress.total > 0) {
        (progress.processed.toFloat() / progress.total.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    AlertDialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
        title = {
            Text(
                text = stringResource(R.string.deleting_contacts_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                if (progress.total > 0) {
                    if (reducedMotion) {
                        LinearProgressIndicator(
                            progress = { fraction },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        LinearWavyProgressIndicator(
                            progress = { fraction },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                Text(
                    text = stringResource(
                        R.string.deleting_progress_count,
                        progress.processed,
                        progress.total
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (progress.currentName.isNotBlank()) {
                    Text(
                        text = progress.currentName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = stringResource(R.string.delete_progress_message),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun DeleteConfirmationDialogLightPreview() {
    DeleteContactsTheme(themeMode = ThemeMode.LIGHT, reducedMotionOverride = true) {
        DeleteConfirmationDialog(
            request = DeleteRequest.DeleteAll(count = 42),
            onDismiss = {},
            onConfirm = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteSelectedConfirmationDialogDarkPreview() {
    DeleteContactsTheme(themeMode = ThemeMode.DARK, reducedMotionOverride = true) {
        DeleteConfirmationDialog(
            request = DeleteRequest.DeleteSelected(
                contacts = listOf(
                    ContactInfo(1L, "Alice", "k1", false),
                    ContactInfo(2L, "Bob", "k2", false)
                )
            ),
            onDismiss = {},
            onConfirm = {}
        )
    }
}
