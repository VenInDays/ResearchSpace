package com.researchspace.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.researchspace.ui.theme.RSColors
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * The Orbital Hub — a circular navigation element positioned at center-bottom.
 * Expands into a radial menu with haptic feedback on interaction.
 * Anti-Material: no FAB, no BottomBar — just this orbital control.
 */

enum class OrbitalAction {
    ADD_NOTE, SEARCH, BOOKMARKS, SETTINGS
}

data class OrbitalMenuItem(
    val action: OrbitalAction,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

@Composable
fun OrbitalHub(
    onAction: (OrbitalAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var isExpanded by remember { mutableStateOf(false) }
    val expandAnim by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "orbital_expand"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (isExpanded) 1.1f else 1f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "orbital_scale"
    )

    val menuItems = listOf(
        OrbitalMenuItem(OrbitalAction.ADD_NOTE, Icons.Default.Add, "Note"),
        OrbitalMenuItem(OrbitalAction.SEARCH, Icons.Default.Search, "Search"),
        OrbitalMenuItem(OrbitalAction.BOOKMARKS, Icons.Default.Bookmark, "Marks"),
        OrbitalMenuItem(OrbitalAction.SETTINGS, Icons.Default.Settings, "Prefs")
    )

    val radius = 90f // radial distance in dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Radial menu items
        menuItems.forEachIndexed { index, item ->
            val angle = -Math.PI / 2 + (index * (Math.PI * 2 / menuItems.size))
            val animatedRadius = radius * expandAnim
            val offsetX = (animatedRadius * cos(angle)).toFloat().dp
            val offsetY = (animatedRadius * sin(angle)).toFloat().dp

            val itemAlpha by animateFloatAsState(
                targetValue = if (isExpanded) 1f else 0f,
                animationSpec = tween(
                    durationMillis = 200,
                    delayMillis = index * 40
                ),
                label = "item_alpha_$index"
            )

            val itemScale by animateFloatAsState(
                targetValue = if (isExpanded) 1f else 0.3f,
                animationSpec = tween(
                    durationMillis = 250,
                    delayMillis = index * 40,
                    easing = FastOutSlowInEasing
                ),
                label = "item_scale_$index"
            )

            Box(
                modifier = Modifier
                    .offset(x = offsetX, y = offsetY - 28.dp)
                    .graphicsLayer {
                        alpha = itemAlpha
                        scaleX = itemScale
                        scaleY = itemScale
                    }
                    .size(48.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(RSColors.OffWhite)
                    .pointerInput(isExpanded) {
                        if (isExpanded) {
                            detectTapGestures(
                                onTap = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onAction(item.action)
                                    isExpanded = false
                                }
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = RSColors.InkBlack,
                    modifier = Modifier.size(22.dp)
                )
                if (isExpanded) {
                    Text(
                        text = item.label,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = RSColors.MutedText,
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .padding(bottom = 2.dp)
                    )
                }
            }
        }

        // Central orb — the main tap target
        Box(
            modifier = Modifier
                .size(56.dp)
                .graphicsLayer {
                    scaleX = scaleAnim
                    scaleY = scaleAnim
                }
                .shadow(
                    elevation = if (isExpanded) 2.dp else 12.dp,
                    shape = CircleShape,
                    ambientColor = RSColors.ShadowDark,
                    spotColor = RSColors.ShadowDark
                )
                .drawBehind {
                    // Neumorphism-inspired ring
                    drawCircle(
                        color = RSColors.SubtleGrey,
                        radius = 30.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = RSColors.OffWhite,
                        radius = 28.dp.toPx(),
                        center = center
                    )
                }
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            RSColors.OrbFill,
                            Color(0xFF2D2D2F)
                        )
                    )
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            isExpanded = !isExpanded
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Inner orb indicator — rotates when expanded
            val rotation by animateFloatAsState(
                targetValue = if (isExpanded) 45f else 0f,
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                label = "orb_rotation"
            )
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer {
                        rotationZ = rotation
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                RSColors.AccentWarm,
                                Color(0xFFE8D5C0)
                            )
                        )
                    )
            )
        }
    }

    // Auto-collapse after 5 seconds
    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            delay(5000)
            isExpanded = false
        }
    }
}
