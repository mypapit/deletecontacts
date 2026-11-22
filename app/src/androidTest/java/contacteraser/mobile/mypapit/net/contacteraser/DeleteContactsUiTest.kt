package contacteraser.mobile.mypapit.net.contacteraser

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactInfo
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactSummary
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ThemeMode
import contacteraser.mobile.mypapit.net.contacteraser.ui.components.DeleteConfirmationDialog
import contacteraser.mobile.mypapit.net.contacteraser.ui.screens.ContactsScreen
import contacteraser.mobile.mypapit.net.contacteraser.ui.screens.OverviewScreen
import contacteraser.mobile.mypapit.net.contacteraser.ui.screens.SettingsScreen
import contacteraser.mobile.mypapit.net.contacteraser.ui.state.ContactsUiState
import contacteraser.mobile.mypapit.net.contacteraser.ui.state.DeleteRequest
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.DeleteContactsTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeleteContactsUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun overviewScreen_showsPermissionPromptWhenMissingReadPermission() {
        var requestedPermission = false
        composeRule.setContent {
            DeleteContactsTheme(themeMode = ThemeMode.LIGHT, reducedMotionOverride = true) {
                OverviewScreen(
                    uiState = ContactsUiState(hasReadPermission = false),
                    onRefresh = {},
                    onRequestReadPermission = { requestedPermission = true },
                    onOpenSettings = {},
                    onInspectContacts = {},
                    onDeleteWithoutPhone = {},
                    onDeleteAll = {}
                )
            }
        }

        val title = composeRule.activity.getString(R.string.permission_title)
        val grantAction = composeRule.activity.getString(R.string.grant_permission_action)
        composeRule.onNodeWithText(title).assertExists()
        composeRule.onNodeWithText(grantAction).performClick()
        assertTrue(requestedPermission)
    }

    @Test
    fun contactsScreen_allowsSelectionAndShowsDeleteAction() {
        var uiState by mutableStateOf(
            ContactsUiState(
                hasReadPermission = true,
                summary = ContactSummary(totalContacts = 2, contactsWithoutPhone = 2),
                contacts = listOf(
                    ContactInfo(1L, "Alice Tan", "k1", false),
                    ContactInfo(2L, "Bob Lim", "k2", false)
                )
            )
        )
        var deleteSelectedInvoked = false

        composeRule.setContent {
            DeleteContactsTheme(themeMode = ThemeMode.LIGHT, reducedMotionOverride = true) {
                ContactsScreen(
                    uiState = uiState,
                    onRefresh = {},
                    onRequestReadPermission = {},
                    onOpenSettings = {},
                    onFilterChange = {},
                    onSearchQueryChange = {},
                    onToggleSelection = { id ->
                        val updated = if (id in uiState.selectedIds) {
                            uiState.selectedIds - id
                        } else {
                            uiState.selectedIds + id
                        }
                        uiState = uiState.copy(selectedIds = updated)
                    },
                    onSelectAllVisible = {},
                    onClearSelection = {},
                    onToggleExpanded = {},
                    onDeleteSelected = { deleteSelectedInvoked = true },
                    onDeleteWithoutPhone = {}
                )
            }
        }

        val selectAliceCd = composeRule.activity.getString(R.string.select_contact_cd, "Alice Tan")
        composeRule.onNodeWithContentDescription(selectAliceCd).performClick()

        val deleteSelectedLabel = composeRule.activity.getString(R.string.delete_selected_action, 1)
        composeRule.onNodeWithText(deleteSelectedLabel).assertExists().performClick()
        assertTrue(deleteSelectedInvoked)
    }

    @Test
    fun deleteAllConfirmationDialog_requiresCheckboxBeforeConfirming() {
        var confirmed = false
        composeRule.setContent {
            DeleteContactsTheme(themeMode = ThemeMode.DARK, reducedMotionOverride = true) {
                DeleteConfirmationDialog(
                    request = DeleteRequest.DeleteAll(count = 10),
                    onDismiss = {},
                    onConfirm = { confirmed = true }
                )
            }
        }

        val confirmButtonLabel = composeRule.activity.getString(R.string.delete_all)
        val checkboxLabel = composeRule.activity.resources.getQuantityString(
            R.plurals.delete_all_checkbox_label,
            10,
            10
        )
        val warningBadge = composeRule.activity.getString(R.string.irreversible_warning_badge)

        composeRule.onNodeWithText(warningBadge).assertExists()
        composeRule.onNodeWithText(confirmButtonLabel).assertIsNotEnabled()
        composeRule.onNodeWithText(checkboxLabel).performClick()
        composeRule.onNodeWithText(confirmButtonLabel).assertIsEnabled().performClick()
        assertTrue(confirmed)
    }

    @Test
    fun settingsScreen_switchesThemeMode() {
        var selectedMode by mutableStateOf(ThemeMode.SYSTEM)
        composeRule.setContent {
            DeleteContactsTheme(themeMode = selectedMode, reducedMotionOverride = true) {
                SettingsScreen(
                    themeMode = selectedMode,
                    uiState = ContactsUiState(hasReadPermission = true, hasWritePermission = true),
                    onThemeModeChange = { selectedMode = it },
                    onRequestPermissions = {},
                    onOpenAppSettings = {}
                )
            }
        }

        val darkLabel = composeRule.activity.getString(R.string.theme_dark)
        val lightLabel = composeRule.activity.getString(R.string.theme_light)
        composeRule.onNodeWithText(darkLabel).performClick()
        assertEquals(ThemeMode.DARK, selectedMode)

        composeRule.onNodeWithText(lightLabel).performClick()
        assertEquals(ThemeMode.LIGHT, selectedMode)
    }
}
