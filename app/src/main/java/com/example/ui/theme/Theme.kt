package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PlayMusicOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3F1D06),
    onPrimaryContainer = PlayMusicAccent,
    secondary = PlayMusicAmber,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF3D2611),
    onSecondaryContainer = Color(0xFFFFDDB8),
    tertiary = PlayMusicAccent,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = PlayMusicOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE0B2),
    onPrimaryContainer = Color(0xFFE65100),
    secondary = PlayMusicOrangeLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFECB3),
    onSecondaryContainer = Color(0xFFFF6F00),
    tertiary = PlayMusicAmber,
    onTertiary = Color.Black,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder
)

@Composable
fun RhythmPlayTheme(
    darkTheme: Boolean = true, // Default to sleek 2016 dark music vibe
    dynamicColor: Boolean = false, // Keep Google Play Music signature orange vibe
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
