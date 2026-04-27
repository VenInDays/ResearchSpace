package com.researchspace.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Editorial = FontFamily.Default
private val Mono = FontFamily.Monospace

val RSTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Editorial, fontWeight = FontWeight.Light,
        fontSize = 34.sp, lineHeight = 42.sp, letterSpacing = (-0.5).sp,
        color = RSColors.InkBlack
    ),
    headlineLarge = TextStyle(
        fontFamily = Editorial, fontWeight = FontWeight.Normal,
        fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = (-0.2).sp,
        color = RSColors.InkBlack
    ),
    headlineMedium = TextStyle(
        fontFamily = Editorial, fontWeight = FontWeight.Medium,
        fontSize = 18.sp, lineHeight = 24.sp, color = RSColors.InkBlack
    ),
    titleLarge = TextStyle(
        fontFamily = Editorial, fontWeight = FontWeight.Medium,
        fontSize = 16.sp, lineHeight = 22.sp, color = RSColors.InkBlack
    ),
    titleMedium = TextStyle(
        fontFamily = Editorial, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 20.sp, color = RSColors.InkBlack
    ),
    bodyLarge = TextStyle(
        fontFamily = Editorial, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, color = RSColors.BodyText
    ),
    bodyMedium = TextStyle(
        fontFamily = Editorial, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, color = RSColors.BodyText
    ),
    bodySmall = TextStyle(
        fontFamily = Editorial, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp, color = RSColors.MutedText
    ),
    labelLarge = TextStyle(
        fontFamily = Editorial, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp,
        color = RSColors.MutedText
    ),
    labelSmall = TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Normal,
        fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 0.3.sp,
        color = RSColors.MonoText
    ),
    // Custom label styles for mono-spaced data
    displaySmall = TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Normal,
        fontSize = 11.sp, lineHeight = 15.sp, letterSpacing = 0.2.sp,
        color = RSColors.MonoText
    )
)
