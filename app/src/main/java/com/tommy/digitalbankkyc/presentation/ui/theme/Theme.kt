package com.tommy.digitalbankkyc.presentation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = NavyBlue,
    onPrimary = Cloud,
    secondary = AquaBlue,
    tertiary = Mint,
    background = Cloud,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFE4ECF4),
    onSurfaceVariant = Slate,
    error = ErrorRed
)

private val DarkColors = darkColorScheme(
    primary = Mint,
    onPrimary = SurfaceDark,
    secondary = AquaBlue,
    tertiary = Color(0xFF8BE5D6),
    background = SurfaceDark,
    onBackground = Cloud,
    surface = SurfaceDarkVariant,
    onSurface = Cloud,
    surfaceVariant = Color(0xFF263847),
    onSurfaceVariant = Color(0xFFB7C7D6),
    error = Color(0xFFF2B8B5)
)

@Composable
fun DigitalBankKYCTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
