package contacteraser.mobile.mypapit.net.contacteraser.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Blue-based Material 3 palette with high-contrast neutral surfaces and warm amber-copper tertiary accents.
val BluePrimaryLight = Color(0xFF0F4C9C)
val BlueOnPrimaryLight = Color(0xFFFFFFFF)
val BluePrimaryContainerLight = Color(0xFFD7E3FF)
val BlueOnPrimaryContainerLight = Color(0xFF001A41)

val BlueSecondaryLight = Color(0xFF435E88)
val BlueOnSecondaryLight = Color(0xFFFFFFFF)
val BlueSecondaryContainerLight = Color(0xFFDCE6FA)
val BlueOnSecondaryContainerLight = Color(0xFF0B2246)

val AmberTertiaryLight = Color(0xFF964900)
val AmberOnTertiaryLight = Color(0xFFFFFFFF)
val AmberTertiaryContainerLight = Color(0xFFFFDCC2)
val AmberOnTertiaryContainerLight = Color(0xFF311400)

val ErrorLight = Color(0xFFBA1A1A)
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFFFDAD6)
val OnErrorContainerLight = Color(0xFF410002)

val BackgroundLight = Color(0xFFF8FAFF)
val OnBackgroundLight = Color(0xFF171C24)
val SurfaceLight = Color(0xFFF8FAFF)
val OnSurfaceLight = Color(0xFF171C24)
val SurfaceVariantLight = Color(0xFFE0E5F0)
val OnSurfaceVariantLight = Color(0xFF434A57)
val OutlineLight = Color(0xFF727A88)
val OutlineVariantLight = Color(0xFFC4CCD9)

val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
val SurfaceContainerLowLight = Color(0xFFF2F5FC)
val SurfaceContainerLight = Color(0xFFECF0F8)
val SurfaceContainerHighLight = Color(0xFFE6EBF5)
val SurfaceContainerHighestLight = Color(0xFFDFE5F0)

val InverseSurfaceLight = Color(0xFF2C313A)
val InverseOnSurfaceLight = Color(0xFFEEF1F8)
val InversePrimaryLight = Color(0xFFAEC7FF)

// Dark color scheme tokens
val BluePrimaryDark = Color(0xFFAEC7FF)
val BlueOnPrimaryDark = Color(0xFF002E6C)
val BluePrimaryContainerDark = Color(0xFF004397)
val BlueOnPrimaryContainerDark = Color(0xFFD7E3FF)

val BlueSecondaryDark = Color(0xFFABC7F7)
val BlueOnSecondaryDark = Color(0xFF102E57)
val BlueSecondaryContainerDark = Color(0xFF2A456F)
val BlueOnSecondaryContainerDark = Color(0xFFDCE6FA)

val AmberTertiaryDark = Color(0xFFFFB780)
val AmberOnTertiaryDark = Color(0xFF512400)
val AmberTertiaryContainerDark = Color(0xFF733500)
val AmberOnTertiaryContainerDark = Color(0xFFFFDCC2)

val ErrorDark = Color(0xFFFFB4AB)
val OnErrorDark = Color(0xFF690005)
val ErrorContainerDark = Color(0xFF93000A)
val OnErrorContainerDark = Color(0xFFFFDAD6)

val BackgroundDark = Color(0xFF0F141C)
val OnBackgroundDark = Color(0xFFE2E6F0)
val SurfaceDark = Color(0xFF0F141C)
val OnSurfaceDark = Color(0xFFE2E6F0)
val SurfaceVariantDark = Color(0xFF424956)
val OnSurfaceVariantDark = Color(0xFFC2C9D6)
val OutlineDark = Color(0xFF8C94A2)
val OutlineVariantDark = Color(0xFF424956)

val SurfaceContainerLowestDark = Color(0xFF0A0E14)
val SurfaceContainerLowDark = Color(0xFF171C25)
val SurfaceContainerDark = Color(0xFF1B212B)
val SurfaceContainerHighDark = Color(0xFF252B36)
val SurfaceContainerHighestDark = Color(0xFF303743)

val InverseSurfaceDark = Color(0xFFE2E6F0)
val InverseOnSurfaceDark = Color(0xFF2C313A)
val InversePrimaryDark = Color(0xFF0F4C9C)

val DeleteContactsLightColorScheme: ColorScheme = lightColorScheme(
    primary = BluePrimaryLight,
    onPrimary = BlueOnPrimaryLight,
    primaryContainer = BluePrimaryContainerLight,
    onPrimaryContainer = BlueOnPrimaryContainerLight,
    secondary = BlueSecondaryLight,
    onSecondary = BlueOnSecondaryLight,
    secondaryContainer = BlueSecondaryContainerLight,
    onSecondaryContainer = BlueOnSecondaryContainerLight,
    tertiary = AmberTertiaryLight,
    onTertiary = AmberOnTertiaryLight,
    tertiaryContainer = AmberTertiaryContainerLight,
    onTertiaryContainer = AmberOnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    inversePrimary = InversePrimaryLight
)

val DeleteContactsDarkColorScheme: ColorScheme = darkColorScheme(
    primary = BluePrimaryDark,
    onPrimary = BlueOnPrimaryDark,
    primaryContainer = BluePrimaryContainerDark,
    onPrimaryContainer = BlueOnPrimaryContainerDark,
    secondary = BlueSecondaryDark,
    onSecondary = BlueOnSecondaryDark,
    secondaryContainer = BlueSecondaryContainerDark,
    onSecondaryContainer = BlueOnSecondaryContainerDark,
    tertiary = AmberTertiaryDark,
    onTertiary = AmberOnTertiaryDark,
    tertiaryContainer = AmberTertiaryContainerDark,
    onTertiaryContainer = AmberOnTertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    inversePrimary = InversePrimaryDark
)

data class AvatarColors(
    val container: Color,
    val content: Color
)

@Composable
fun avatarColorForIndex(index: Int, colorScheme: ColorScheme): AvatarColors {
    return when (Math.floorMod(index, 4)) {
        0 -> AvatarColors(colorScheme.primaryContainer, colorScheme.onPrimaryContainer)
        1 -> AvatarColors(colorScheme.tertiaryContainer, colorScheme.onTertiaryContainer)
        2 -> AvatarColors(colorScheme.secondaryContainer, colorScheme.onSecondaryContainer)
        else -> AvatarColors(colorScheme.surfaceContainerHighest, colorScheme.primary)
    }
}
