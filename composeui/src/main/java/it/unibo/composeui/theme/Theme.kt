package it.unibo.composeui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF0D47A1)
val Secondary = Color(0xFF1565C0)
val Background = Color(0xFFF5F5F5)
val Surface = Color.White
val Error = Color(0xFFB00020)

val OnPrimary = Color.White
val OnSecondary = Color.White
val OnBackground = Color.Black
val OnSurface = Color.Black
val OnError = Color.White

private val LightColors = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    background = Background,
    surface = Surface,
    error = Error,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onBackground = OnBackground,
    onSurface = OnSurface,
    onError = OnError
)

private val DarkColors = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = Error,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = OnError
)

@Composable
fun MoneyRateTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}
