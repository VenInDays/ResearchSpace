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

enum class OrbitalAction { ADD_NOTE, SEARCH, BOOKMARKS, SETTINGS }

@Composable
fun OrbitalHub(
    onAction: (OrbitalAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var isExpanded by remember { mutableStateOf(false) }
    val expandAnim by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "orb"
    )
    val scaleAnim by animateFloatAsState(
        targetValue = if (isExpanded) 1.08f else 1f,
        animationSpec = tween(250, easing = FastOutSlowInEasing), label = "orb_scale"
    )

    val items = listOf(
        OrbitalAction.ADD_NOTE to Icons.Default.Add,
        OrbitalAction.SEARCH to Icons.Default.Search,
        OrbitalAction.BOOKMARKS to Icons.Default.Bookmark,
        OrbitalAction.SETTINGS to Icons.Default.Settings
    )
    val radius = 88f

    Box(modifier = modifier.fillMaxWidth().height(210.dp), contentAlignment = Alignment.BottomCenter) {
        items.forEachIndexed { index, (action, icon) ->
            val angle = -Math.PI / 2 + (index * Math.PI * 2 / items.size)
            val r = radius * expandAnim
            val ox = (r * cos(angle)).toFloat().dp
            val oy = (r * sin(angle)).toFloat().dp
            val alpha by animateFloatAsState(
                targetValue = if (isExpanded) 1f else 0f,
                animationSpec = tween(180, delayMillis = index * 35), label = "ia$index"
            )
            val sc by animateFloatAsState(
                targetValue = if (isExpanded) 1f else 0.25f,
                animationSpec = tween(220, delayMillis = index * 35, easing = FastOutSlowInEasing),
                label = "is$index"
            )
            Box(
                modifier = Modifier
                    .offset(x = ox, y = oy - 24.dp)
                    .graphicsLayer { this.alpha = alpha; scaleX = sc; scaleY = sc }
                    .size(44.dp)
                    .shadow(6.dp, CircleShape, ambientColor = RSColors.ShadowAmbient, spotColor = RSColors.ShadowSpot)
                    .clip(CircleShape)
                    .background(RSColors.SurfaceCard)
                    .then(
                        if (isExpanded) Modifier.pointerInput(action) {
                            detectTapGestures {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onAction(action); isExpanded = false
                            }
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = RSColors.BodyText, modifier = Modifier.size(20.dp))
            }
        }
        // Central orb
        Box(
            modifier = Modifier
                .size(52.dp)
                .graphicsLayer { scaleX = scaleAnim; scaleY = scaleAnim }
                .shadow(
                    if (isExpanded) 2.dp else 10.dp, CircleShape,
                    ambientColor = RSColors.ShadowAmbient, spotColor = RSColors.ShadowSpot
                )
                .drawBehind {
                    drawCircle(color = RSColors.NeuDark, radius = 27.dp.toPx(), center = center)
                    drawCircle(color = RSColors.OffWhite, radius = 26.dp.toPx(), center = center)
                }
                .clip(CircleShape)
                .background(Brush.radialGradient(
                    colors = listOf(RSColors.InkBlack, Color(0xFF3A3A3C))
                ))
                .pointerInput(Unit) {
                    detectTapGestures {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        isExpanded = !isExpanded
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            val rot by animateFloatAsState(
                targetValue = if (isExpanded) 45f else 0f,
                animationSpec = tween(250, easing = FastOutSlowInEasing), label = "rot"
            )
            Box(
                modifier = Modifier.size(18.dp).graphicsLayer { rotationZ = rot }
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(RSColors.AccentWarm, Color(0xFFE8D5C0))))
            )
        }
    }
    LaunchedEffect(isExpanded) { if (isExpanded) delay(5000); isExpanded = false }
}
