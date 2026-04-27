package com.researchspace.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = RSColors.InkBlack,
    onPrimary = RSColors.OffWhite,
    secondary = RSColors.AccentWarm,
    onSecondary = RSColors.InkBlack,
    background = RSColors.OffWhite,
    onBackground = RSColors.InkBlack,
    surface = RSColors.OffWhite,
    onSurface = RSColors.InkBlack,
    surfaceVariant = RSColors.NeuSurface,
    outline = RSColors.SubtleGrey,
    outlineVariant = RSColors.CardBorder
)

@Composable
fun ResearchSpaceTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = RSColors.OffWhite.toArgb()
            window.navigationBarColor = RSColors.OffWhite.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RSTypography,
        content = content
    )
}
