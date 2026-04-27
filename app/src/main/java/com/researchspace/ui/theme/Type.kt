package com.researchspace.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Using system default (Roboto on Android) with carefully tuned weights
// for a "tactile editorial" feel — no rounded fonts, no display fonts.

private val Editorial = FontFamily.Default

val RSTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Editorial,
        fontWeight = FontWeight.Light,
        fontSize = 34.sp,
        lineHeight = 42.sp,
        letterSpacing = (-0.5).sp,
        color = RSColors.InkBlack
    ),
    displayMedium = TextStyle(
        fontFamily = Editorial,
        fontWeight = FontWeight.Light,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.3).sp,
        color = RSColors.InkBlack
    ),
    headlineLarge = TextStyle(
        fontFamily = Editorial,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.2).sp,
        color = RSColors.InkBlack
    ),
    headlineMedium = TextStyle(
        fontFamily = Editorial,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        color = RSColors.InkBlack
    ),
    titleLarge = TextStyle(
        fontFamily = Editorial,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        color = RSColors.InkBlack
    ),
    titleMedium = TextStyle(
        fontFamily = Editorial,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = RSColors.InkBlack
    ),
    bodyLarge = TextStyle(
        fontFamily = Editorial,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = RSColors.InkBlack
    ),
    bodyMedium = TextStyle(
        fontFamily = Editorial,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = RSColors.InkBlack
    ),
    bodySmall = TextStyle(
        fontFamily = Editorial,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = RSColors.MutedText
    ),
    labelLarge = TextStyle(
        fontFamily = Editorial,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = RSColors.MutedText
    ),
    labelSmall = TextStyle(
        fontFamily = Editorial,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.3.sp,
        color = RSColors.MutedText
    )
)
