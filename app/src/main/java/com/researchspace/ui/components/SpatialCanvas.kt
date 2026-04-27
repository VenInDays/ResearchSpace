package com.researchspace.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.researchspace.data.LinkMetadata
import com.researchspace.data.ResearchNote
import com.researchspace.ui.theme.RSColors

/**
 * Spatial Canvas v2 — infinite 2D pan/zoom canvas.
 * Features: grain dot-grid, ultra-soft paper-stack shadows, source markers, inline expansion.
 */
@Composable
fun SpatialCanvas(
    notes: List<ResearchNote>,
    activeNoteId: String?,
    onNoteClick: (ResearchNote) -> Unit,
    onNoteDrag: (String, Float, Float) -> Unit,
    onCanvasTap: (Offset) -> Unit,
    noteContent: @Composable (ResearchNote) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RSColors.OffWhite)
            // Grain texture background
            .drawBehind {
                val dotSpacing = 20.dp.toPx()
                val dotRadius = 0.4.dp.toPx()
                val startX = (offsetX % dotSpacing) - dotSpacing
                val startY = (offsetY % dotSpacing) - dotSpacing
                var x = startX
                while (x < size.width) {
                    var y = startY
                    while (y < size.height) {
                        drawCircle(color = RSColors.GrainLight, radius = dotRadius, center = Offset(x, y))
                        y += dotSpacing
                    }
                    x += dotSpacing
                }
                // Ultra-subtle random grain
                drawRect(color = RSColors.GrainOverlay, size = size)
            }
            .pointerInput(Unit) { detectTapGestures { onCanvasTap(it) } }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.25f, 3f)
                    offsetX += pan.x; offsetY += pan.y
                    if (zoom != 1f) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            }
    ) {
        Box(
            modifier = Modifier.fillMaxSize().graphicsLayer {
                scaleX = scale; scaleY = scale; translationX = offsetX; translationY = offsetY
            }
        ) {
            notes.sortedByDescending { it.zIndex }.forEach { note ->
                NoteCard(
                    note = note,
                    isActive = note.id == activeNoteId,
                    onClick = { onNoteClick(note) },
                    onDrag = { dx, dy -> onNoteDrag(note.id, dx, dy) },
                    noteContent = noteContent
                )
            }
        }
        if (scale != 1f) {
            Box(
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                    .clip(RoundedCornerShape(6.dp)).background(RSColors.ToolbarGlass)
            ) {
                Text(
                    text = "${(scale * 100).toInt()}%",
                    fontSize = 9.sp, fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.Monospace, color = RSColors.MonoText,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun NoteCard(
    note: ResearchNote,
    isActive: Boolean,
    onClick: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    noteContent: @Composable (ResearchNote) -> Unit
) {
    val cardShape = RoundedCornerShape(8.dp)
    val haptic = LocalHapticFeedback.current
    var dragX by remember { mutableFloatStateOf(0f) }
    var dragY by remember { mutableFloatStateOf(0f) }

    // Animated height for inline expansion
    val animatedHeight by animateFloatAsState(
        targetValue = note.displayHeight,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 200f), label = "nh"
    )

    Box(
        modifier = Modifier
            .offset(x = (note.positionX + dragX).dp, y = (note.positionY + dragY).dp)
            .width(note.width.dp)
            .height(animatedHeight.dp)
            .shadow(
                if (isActive) 16.dp else 6.dp, cardShape,
                ambientColor = RSColors.ShadowAmbient, spotColor = RSColors.ShadowSpot
            )
            .drawBehind {
                // Paper stack: bottom sheet offset
                drawRoundRect(
                    color = RSColors.NeuDark.copy(alpha = 0.06f),
                    topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
                    size = Size(size.width, size.height),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
                // Subtle grain on card
                drawRoundRect(
                    color = RSColors.GrainMedium,
                    size = size,
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
            }
            .clip(cardShape)
            .background(RSColors.SurfaceCard)
            .border(
                width = 0.5.dp,
                color = if (isActive) RSColors.AccentWarm.copy(alpha = 0.5f) else RSColors.SubtleBorder,
                shape = cardShape
            )
            .pointerInput(note.id) {
                detectDragGestures(
                    onDragStart = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragX += dragAmount.x; dragY += dragAmount.y
                    },
                    onDragEnd = { onDrag(dragX, dragY); dragX = 0f; dragY = 0f }
                )
            }
            .pointerInput(note.id) {
                detectTapGestures(onTap = { onClick() })
            }
            .padding(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            noteContent(note)

            // Source markers — bottom-right stacking
            if (note.links.isNotEmpty()) {
                SourceMarkers(
                    links = note.links,
                    expanded = note.isExpanded,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }
    }
}

/**
 * Source Markers — 24dp rounded-square icons with soft blue (#A1C4FD) finish.
 * Stack neatly in the bottom-right corner. Clicking opens detail view.
 */
@Composable
fun SourceMarkers(
    links: List<LinkMetadata>,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    onSourceClick: ((String) -> Unit)? = null
) {
    val visibleCount = if (expanded) links.size else minOf(links.size, 3)
    val markerSize = 24.dp
    val overlap = if (visibleCount > 1) 8 else 0

    Row(
        modifier = modifier.padding(top = 6.dp),
        horizontalArrangement = Arrangement.spacedBy((-overlap).dp),
        verticalAlignment = Alignment.Bottom
    ) {
        links.take(visibleCount).forEachIndexed { index, link ->
            val offset by animateFloatAsState(
                targetValue = if (expanded) 0f else (index * 2f),
                animationSpec = tween(200, easing = FastOutSlowInEasing),
                label = "sm_$index"
            )
            Box(
                modifier = Modifier
                    .size(markerSize)
                    .graphicsLayer { translationY = offset }
                    .shadow(
                        4.dp, RoundedCornerShape(6.dp),
                        ambientColor = RSColors.ShadowAmbient, spotColor = RSColors.ShadowSpot
                    )
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (link.hasImage) RSColors.SourceBlue
                        else RSColors.SourceBlueSoft
                    )
                    .then(
                        if (onSourceClick != null) Modifier.pointerInput(Unit) {
                            detectTapGestures { onSourceClick(link.id) }
                        } else Modifier
                    )
            ) {
                if (link.hasImage) {
                    // First letter of domain
                    Text(
                        text = link.domain.take(1).uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    Icon(
                        Icons.Default.Link, contentDescription = null,
                        tint = RSColors.SourceBlue,
                        modifier = Modifier.size(12.dp).align(Alignment.Center)
                    )
                }
            }
        }
        // Overflow indicator
        if (links.size > visibleCount) {
            Box(
                modifier = Modifier
                    .size(markerSize)
                    .clip(RoundedCornerShape(6.dp))
                    .background(RSColors.NeuDark.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+${links.size - visibleCount}",
                    fontSize = 9.sp, fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    color = RSColors.MonoText
                )
            }
        }
    }
}
