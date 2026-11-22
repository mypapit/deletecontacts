package contacteraser.mobile.mypapit.net.contacteraser

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import contacteraser.mobile.mypapit.net.contacteraser.data.preferences.DataStoreThemePreferencesRepository
import contacteraser.mobile.mypapit.net.contacteraser.data.repository.AndroidContactRepository
import contacteraser.mobile.mypapit.net.contacteraser.ui.DeleteContactsApp
import contacteraser.mobile.mypapit.net.contacteraser.ui.state.ContactsViewModel
import contacteraser.mobile.mypapit.net.contacteraser.ui.state.ThemeViewModel
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.DeleteContactsTheme
import contacteraser.mobile.mypapit.net.contacteraser.ui.theme.resolveDarkTheme

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels {
        ThemeViewModel.Factory(DataStoreThemePreferencesRepository(applicationContext))
    }

    private val contactsViewModel: ContactsViewModel by viewModels {
        ContactsViewModel.Factory(AndroidContactRepository(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
            val isDarkTheme = resolveDarkTheme(themeMode)

            LaunchedEffect(isDarkTheme) {
                val barStyle = if (isDarkTheme) {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                }
                enableEdgeToEdge(
                    statusBarStyle = barStyle,
                    navigationBarStyle = barStyle
                )
            }

            DeleteContactsTheme(themeMode = themeMode) {
                DeleteContactsApp(
                    activity = this,
                    contactsViewModel = contactsViewModel,
                    themeViewModel = themeViewModel,
                    themeMode = themeMode
                )
            }
        }
    }
}
