package com.researchspace.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.researchspace.ui.theme.RSColors

/**
 * Glassmorphism Floating Toolbar v2.
 * - 20% of screen width
 * - Translucent frosted-glass background
 * - Absolute-positioned overlay floating over the top edge of the active note
 * - Minimal, clean, never pushed off-screen
 */
enum class ToolbarAction { BOLD, ITALIC, UNDERLINE, LINK, CODE, LIST, IMAGE, MORE }

@Composable
fun FloatingToolbar(
    onAction: (ToolbarAction) -> Unit,
    visible: Boolean,
    modifier: Modifier = Modifier,
    activeActions: Set<ToolbarAction> = emptySet()
) {
    val haptic = LocalHapticFeedback.current

    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 60f,
        animationSpec = spring(dampingRatio = 0.9f, stiffness = 250f), label = "tb_y"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(180), label = "tb_a"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.92f,
        animationSpec = tween(200, easing = FastOutSlowInEasing), label = "tb_s"
    )

    val buttons = listOf(
        ToolbarAction.BOLD to Icons.Default.FormatBold,
        ToolbarAction.ITALIC to Icons.Default.FormatItalic,
        ToolbarAction.UNDERLINE to Icons.Default.FormatUnderlined,
        ToolbarAction.LINK to Icons.Default.Link,
        ToolbarAction.CODE to Icons.Default.Code,
        ToolbarAction.LIST to Icons.Default.CheckBox,
        ToolbarAction.IMAGE to Icons.Default.Image,
        ToolbarAction.MORE to Icons.Default.MoreHoriz
    )

    Box(
        modifier = modifier
            .fillMaxWidth(0.2f)  // 20% width
            .graphicsLayer { translationY = offsetY; this.alpha = alpha; scaleX = scale; scaleY = scale }
            .offset(y = (-4).dp)  // Float slightly above the anchor point
    ) {
        // Glass container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = RSColors.ShadowAmbient.copy(alpha = 0.5f),
                    spotColor = RSColors.ShadowSpot.copy(alpha = 0.2f)
                )
                .clip(RoundedCornerShape(12.dp))
                .background(RSColors.ToolbarGlass)  // 80% translucent
                .border(0.5.dp, RSColors.GlassBorder, RoundedCornerShape(12.dp))
        ) {
            // Single-column vertical toolbar to fit narrow width
            Column(
                modifier = Modifier.fillMaxSize().padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // First 4 buttons (top row of vertical)
                buttons.take(4).forEach { (action, icon) ->
                    val isActive = activeActions.contains(action)
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isActive) RSColors.InkBlack.copy(alpha = 0.06f) else Color.Transparent)
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onAction(action)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon, contentDescription = null,
                            tint = if (isActive) RSColors.ToolbarActive else RSColors.MutedText,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }

        // Second column for remaining buttons (to the right)
        Box(
            modifier = Modifier
                .offset(x = (36).dp, y = 4.dp)
                .width(36.dp)
                .height(36.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(10.dp),
                    ambientColor = RSColors.ShadowAmbient.copy(alpha = 0.4f),
                    spotColor = RSColors.ShadowSpot.copy(alpha = 0.15f)
                )
                .clip(RoundedCornerShape(10.dp))
                .background(RSColors.ToolbarGlass)
                .border(0.5.dp, RSColors.GlassBorder, RoundedCornerShape(10.dp))
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            // More actions as a compact grid
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
                buttons.drop(4).forEach { (action, icon) ->
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onAction(action)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = RSColors.MutedText, modifier = Modifier.size(12.dp))
                    }
                }
            }
        }

        // Paper-tear edge indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.5f)
                .height(0.5.dp)
                .background(Brush.horizontalGradient(listOf(Color.Transparent, RSColors.SubtleBorder, Color.Transparent)))
        )
    }
}
