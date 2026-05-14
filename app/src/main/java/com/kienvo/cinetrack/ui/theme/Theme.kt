package com.kienvo.cinetrack.ui.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
// thêm import này ở đầu file
import androidx.compose.ui.graphics.Color

private val CineTrackColorScheme = darkColorScheme(
    primary          = CinemaRed,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFF5C0007),
    onPrimaryContainer = Color(0xFFFFDAD6),

    secondary        = CinemaGold,
    onSecondary      = Color(0xFF3A2F00),

    background       = DarkBg,
    onBackground     = OnDark,

    surface          = DarkSurface,
    onSurface        = OnDark,
    surfaceVariant   = DarkSurface2,
    onSurfaceVariant = OnDarkSecond,

    error            = ErrorRed,
    outline          = Color(0xFF444444)
)

@Composable
fun CineTrackTheme(content: @Composable () -> Unit) {
    val colorScheme = CineTrackColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkBg.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}