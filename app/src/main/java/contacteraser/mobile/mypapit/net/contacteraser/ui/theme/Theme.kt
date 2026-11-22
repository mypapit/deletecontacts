package contacteraser.mobile.mypapit.net.contacteraser.ui.theme

import android.content.Context
import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ThemeMode

/**
 * Exposes whether system-level reduced motion / disabled animations are active.
 */
val LocalReducedMotion = staticCompositionLocalOf { false }

private fun isSystemReducedMotionEnabled(context: Context): Boolean {
    return try {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        ) == 0f
    } catch (_: SecurityException) {
        false
    } catch (_: Throwable) {
        false
    }
}

@Composable
fun resolveDarkTheme(themeMode: ThemeMode): Boolean = when (themeMode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DeleteContactsTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    reducedMotionOverride: Boolean? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val darkTheme = resolveDarkTheme(themeMode)
    val colorScheme = if (darkTheme) {
        DeleteContactsDarkColorScheme
    } else {
        DeleteContactsLightColorScheme
    }
    val reducedMotion = reducedMotionOverride ?: remember(context) {
        isSystemReducedMotionEnabled(context)
    }
    val motionScheme = if (reducedMotion) {
        MotionScheme.standard()
    } else {
        MotionScheme.expressive()
    }

    CompositionLocalProvider(LocalReducedMotion provides reducedMotion) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            motionScheme = motionScheme,
            shapes = DeleteContactsShapes,
            typography = DeleteContactsTypography,
            content = content
        )
    }
}
