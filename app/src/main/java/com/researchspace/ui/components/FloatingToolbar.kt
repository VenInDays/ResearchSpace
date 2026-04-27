package com.researchspace.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.researchspace.ui.theme.RSColors

/**
 * Floating Toolbar — sits 20% over the edge of the active Note editor.
 * Creates a non-traditional "stuck" look, as if the toolbar is half inside
 * and half outside the note card boundary.
 *
 * Anti-Material: No standard Toolbar, no TopAppBar.
 * Tactile: Soft neumorphic shadows, 0.5dp subtle border.
 */

enum class ToolbarAction {
    BOLD, ITALIC, UNDERLINE, LINK, IMAGE, CODE, CHECKLIST, MORE
}

data class ToolbarButton(
    val action: ToolbarAction,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
    val isActive: Boolean = false
)

@Composable
fun FloatingToolbar(
    onAction: (ToolbarAction) -> Unit,
    visible: Boolean,
    modifier: Modifier = Modifier,
    activeActions: Set<ToolbarAction> = emptySet()
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current

    // Slide in/out animation
    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 80f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "toolbar_slide"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(200),
        label = "toolbar_alpha"
    )

    val buttons = listOf(
        ToolbarButton(ToolbarAction.BOLD, Icons.Default.FormatBold, "B"),
        ToolbarButton(ToolbarAction.ITALIC, Icons.Default.FormatItalic, "I"),
        ToolbarButton(ToolbarAction.UNDERLINE, Icons.Default.FormatUnderlined, "U"),
        ToolbarButton(ToolbarAction.LINK, Icons.Default.Link, "Link"),
        ToolbarButton(ToolbarAction.IMAGE, Icons.Default.Image, "Img"),
        ToolbarButton(ToolbarAction.CODE, Icons.Default.Code, "Code"),
        ToolbarButton(ToolbarAction.CHECKLIST, Icons.Default.CheckBox, "List"),
        ToolbarButton(ToolbarAction.MORE, Icons.Default.MoreHoriz, "More")
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
            .graphicsLayer {
                translationY = offsetY
                this.alpha = alpha
            }
    ) {
        // Main toolbar container with neumorphic shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .graphicsLayer {
                    translationY = -6.dp.toPx() // Float 20% over the edge
                }
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(14.dp),
                    ambientColor = RSColors.ShadowDark.copy(alpha = 0.3f),
                    spotColor = RSColors.ShadowDark.copy(alpha = 0.1f)
                )
                .drawBehind {
                    // Neumorphism: light shadow (top-left)
                    drawRoundRect(
                        color = RSColors.ShadowLight,
                        topLeft = Offset(-2.dp.toPx(), -2.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(
                            size.width + 4.dp.toPx(),
                            size.height + 4.dp.toPx()
                        ),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx())
                    )
                }
                .clip(RoundedCornerShape(14.dp))
                .background(RSColors.ToolbarBg)
                .border(
                    width = 0.5.dp,
                    color = RSColors.ToolbarBorder,
                    shape = RoundedCornerShape(14.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                buttons.forEach { button ->
                    val isActive = activeActions.contains(button.action)

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isActive) RSColors.InkBlack.copy(alpha = 0.08f)
                                else Color.Transparent
                            )
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    onDrag = { _, _ -> }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = button.icon,
                            contentDescription = button.label,
                            tint = if (isActive) RSColors.InkBlack else RSColors.MutedText,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(2.dp))
                }
            }
        }

        // "Paper tear" indicator at the overlap edge
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.4f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            RSColors.SubtleGrey,
                            Color.Transparent
                        )
                    )
                )
        )
    }
}
