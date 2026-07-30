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
import com.example.data.model.AppThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color(0xFF381E72),
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = IndigoSecondary,
    tertiary = CyanAccent,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant
)

private val AmoledColorScheme = darkColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color(0xFF381E72),
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = IndigoSecondary,
    tertiary = EmeraldGreen,
    background = AmoledBackground,
    surface = AmoledSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = AmoledOnSurface,
    onSurface = AmoledOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant
)

private val LightColorScheme = darkColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color(0xFF381E72),
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = IndigoSecondary,
    tertiary = CyanAccent,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant
)

@Composable
fun ScreenTimeTrackerTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> systemInDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK, AppThemeMode.AMOLED -> true
    }

    val colorScheme = when {
        themeMode == AppThemeMode.AMOLED -> AmoledColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

