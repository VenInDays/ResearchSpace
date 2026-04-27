package com.researchspace.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Tactile Minimalism v2 — Muted organic palette.
 * No neon, no cyberpunk, no vibrant gradients.
 * Warm paper tones with ultra-soft shadows.
 */
object RSColors {
    // Backgrounds
    val OffWhite       = Color(0xFFF5F5F0)     // Warm paper
    val Paper          = Color(0xFFEDEDE8)     // Slightly warmer
    val SurfaceCard    = Color(0xFFFAFAF5)
    val GrainOverlay   = Color(0x0D000000)     // Ultra-subtle grain

    // Text
    val InkBlack       = Color(0xFF1D1D1F)
    val BodyText       = Color(0xFF2C2C2E)
    val MutedText      = Color(0xFF8E8E93)
    val FaintText      = Color(0xFFAEAEB2)
    val MonoText       = Color(0xFF636366)     // For timestamps/URLs

    // Borders
    val SubtleBorder   = Color(0x33C7C7CC)     // 0.5dp equivalent
    val SoftBorder     = Color(0x1AD1D1D6)

    // Shadows (neumorphic, ultra-soft)
    val NeuLight       = Color(0xFFFFFFFF)
    val NeuDark        = Color(0xFFD8D8DC)
    val ShadowAmbient  = Color(0x1A000000)
    val ShadowSpot     = Color(0x0D000000)

    // Accent
    val SourceBlue     = Color(0xFFA1C4FD)     // Source marker
    val SourceBlueSoft = Color(0xFFD6E4FF)
    val AccentWarm     = Color(0xFFC4A882)
    val LinkOverlay    = Color(0x88000000)     // Darker for text readability

    // Glassmorphism
    val GlassBg        = Color(0xB3F5F5F0)     // 70% translucent off-white
    val GlassBorder    = Color(0x4DC7C7CC)
    val GlassBlur      = Color(0x66FFFFFF)

    // Toolbar
    val ToolbarGlass   = Color(0xCCF5F5F0)     // 80% translucent
    val ToolbarActive  = Color(0xE01D1D1F)

    // Grain texture layers
    val GrainLight     = Color(0x08000000)
    val GrainMedium    = Color(0x12000000)
}
