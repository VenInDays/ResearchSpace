package com.researchspace.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.researchspace.data.ResearchNote
import com.researchspace.ui.theme.RSColors

/**
 * Spatial Canvas — a 2D infinite canvas where notes are placed freely.
 * Supports pan (drag) and zoom (pinch).
 * Anti-Material: Not a LazyColumn list. Notes are spatially organized.
 * Tactile: Grainy dot-grid background, soft paper-stack layering.
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
    // Transform state
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RSColors.OffWhite)
            // Grain dot grid background
            .drawBehind {
                val dotSpacing = 24.dp.toPx()
                val dotRadius = 0.5.dp.toPx()
                val dotColor = RSColors.SubtleGrey

                val startX = (offsetX % dotSpacing) - dotSpacing
                val startY = (offsetY % dotSpacing) - dotSpacing

                for (x in startX..size.width step dotSpacing) {
                    for (y in startY..size.height step dotSpacing) {
                        drawCircle(
                            color = dotColor,
                            radius = dotRadius,
                            center = Offset(x, y)
                        )
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onCanvasTap(offset)
                }
            }
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.3f, 3f)
                    offsetX += pan.x
                    offsetY += pan.y

                    if (zoom != 1f) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                }
            }
    ) {
        // Notes layer with transform
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                }
        ) {
            notes
                .sortedByDescending { it.zIndex }
                .forEach { note ->
                    NoteCard(
                        note = note,
                        isActive = note.id == activeNoteId,
                        onClick = { onNoteClick(note) },
                        onDrag = { dx, dy ->
                            onNoteDrag(note.id, dx, dy)
                        },
                        noteContent = noteContent
                    )
                }
        }

        // Zoom indicator
        if (scale != 1f) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                color = RSColors.ToolbarBg.copy(alpha = 0.9f)
            ) {
                Text(
                    text = "${(scale * 100).toInt()}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = RSColors.MutedText,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Individual note card on the spatial canvas.
 * Has a paper-stack visual with layered shadows.
 */
@Composable
private fun NoteCard(
    note: ResearchNote,
    isActive: Boolean,
    onClick: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    noteContent: @Composable (ResearchNote) -> Unit
) {
    val cardShape = RoundedCornerShape(10.dp)
    val haptic = LocalHapticFeedback.current

    // Drag state
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .offset(
                x = (note.positionX + dragOffsetX).dp,
                y = (note.positionY + dragOffsetY).dp
            )
            .width(note.width.dp)
            .defaultMinSize(minHeight = note.height.dp)
            .shadow(
                elevation = if (isActive) 20.dp else 8.dp,
                shape = cardShape,
                ambientColor = RSColors.ShadowDark.copy(alpha = 0.2f),
                spotColor = RSColors.ShadowDark.copy(alpha = 0.08f)
            )
            // Paper stack effect: multiple shadow layers
            .drawBehind {
                // Bottom "sheet" of paper stack
                drawRoundRect(
                    color = RSColors.ShadowDark.copy(alpha = 0.08f),
                    topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(
                        size.width,
                        size.height
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx())
                )
            }
            .clip(cardShape)
            .background(RSColors.CardSurface)
            .border(
                width = 0.5.dp,
                color = if (isActive) RSColors.AccentWarm else RSColors.CardBorder,
                shape = cardShape
            )
            .pointerInput(note.id) {
                detectDragGestures(
                    onDragStart = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffsetX += dragAmount.x
                        dragOffsetY += dragAmount.y
                    },
                    onDragEnd = {
                        onDrag(dragOffsetX, dragOffsetY)
                        dragOffsetX = 0f
                        dragOffsetY = 0f
                    }
                )
            }
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        noteContent(note)
    }
}

/**
 * Default note content composable with title and preview.
 */
@Composable
fun DefaultNoteContent(note: ResearchNote) {
    Column {
        // Note header: timestamp + pin indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = note.formattedTimestamp,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = RSColors.MutedText,
                letterSpacing = 0.3.sp
            )
            if (note.isPinned) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = "Pinned",
                    tint = RSColors.AccentWarm,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Note body
        if (note.content.isNotBlank()) {
            Text(
                text = note.content,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = RSColors.InkBlack,
                lineHeight = 19.sp,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
        } else {
            Text(
                text = "Empty note…",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = RSColors.MutedText,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }

        // Link count indicator
        if (note.links.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = RSColors.InkBlack.copy(alpha = 0.04f)
            ) {
                Text(
                    text = "${note.links.size} link${if (note.links.size > 1) "s" else ""} attached",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = RSColors.MutedText,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}
