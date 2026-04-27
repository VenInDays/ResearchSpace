package com.researchspace.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.researchspace.data.LinkMetadata
import com.researchspace.data.ResearchNote
import com.researchspace.ui.components.*
import com.researchspace.ui.theme.RSColors
import com.researchspace.viewmodel.ResearchViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ResearchSpaceScreen(viewModel: ResearchViewModel) {
    val notes by viewModel.notes.collectAsState()
    val activeNoteId by viewModel.activeNoteId.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val focusedSourceId by viewModel.focusedSourceId.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(toastMessage) {
        toastMessage?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show(); viewModel.clearToast() }
    }

    Box(modifier = Modifier.fillMaxSize().background(RSColors.OffWhite)) {
        // Spatial Canvas
        SpatialCanvas(
            notes = notes,
            activeNoteId = activeNoteId,
            onNoteClick = { note ->
                viewModel.selectNote(note)
                viewModel.bringToFront(note.id)
            },
            onNoteDrag = { id, dx, dy -> viewModel.dragNote(id, dx, dy) },
            onCanvasTap = { viewModel.deselectNote() },
            noteContent = { note ->
                InlineNoteContent(
                    note = note,
                    isActive = note.id == activeNoteId,
                    onContentChange = { viewModel.updateNoteContent(note.id, it) },
                    onExpandToggle = { viewModel.toggleExpand(note.id) },
                    onSourceClick = { viewModel.focusSource(it) },
                    onRemoveLink = { viewModel.removeLink(note.id, it) },
                    onDelete = { viewModel.deleteNote(note.id) },
                    onPinToggle = { viewModel.togglePin(note.id) },
                    onClose = { viewModel.deselectNote() }
                )
            },
            modifier = Modifier.fillMaxSize()
        )

        if (notes.isEmpty()) {
            EmptyCanvasState(modifier = Modifier.align(Alignment.Center))
        }

        // Floating Toolbar — absolute positioned overlay
        val activeNote = notes.find { it.id == activeNoteId }
        FloatingToolbar(
            onAction = { _ -> /* handled by inline editor */ },
            visible = activeNote != null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
        )

        // Source Detail overlay (when a source marker is clicked)
        AnimatedVisibility(
            visible = focusedSourceId != null,
            enter = fadeIn(animationSpec = tween(200)) + scaleIn(
                initialScale = 0.9f, animationSpec = tween(250, easing = FastOutSlowInEasing)
            ),
            exit = fadeOut(animationSpec = tween(150)) + scaleOut(
                targetScale = 0.9f, animationSpec = tween(150)
            ),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 40.dp)
        ) {
            val sourceLink = notes
                .flatMap { it.links }
                .find { it.id == focusedSourceId }
            if (sourceLink != null) {
                SourceDetailView(
                    metadata = sourceLink,
                    onDismiss = { viewModel.clearSourceFocus() }
                )
            }
        }

        // Orbital Hub
        OrbitalHub(
            onAction = { action ->
                when (action) {
                    OrbitalAction.ADD_NOTE -> viewModel.createNote()
                    OrbitalAction.SEARCH -> Toast.makeText(context, "Search coming soon", Toast.LENGTH_SHORT).show()
                    OrbitalAction.BOOKMARKS -> Toast.makeText(context, "Bookmarks coming soon", Toast.LENGTH_SHORT).show()
                    OrbitalAction.SETTINGS -> Toast.makeText(context, "Settings coming soon", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * Inline Note Content — everything rendered within the note card itself.
 * NO BottomSheet. Inline expansion reveals sources and full content.
 * URL detection happens automatically from the text content.
 */
@Composable
private fun InlineNoteContent(
    note: ResearchNote,
    isActive: Boolean,
    onContentChange: (String) -> Unit,
    onExpandToggle: () -> Unit,
    onSourceClick: (String) -> Unit,
    onRemoveLink: (String) -> Unit,
    onDelete: () -> Unit,
    onPinToggle: () -> Unit,
    onClose: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
        // Header row — always visible
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Timestamp — mono-spaced
                Text(
                    text = note.formattedTimestamp,
                    fontSize = 9.sp, fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.Monospace,
                    color = RSColors.MonoText, letterSpacing = 0.2.sp
                )
                if (note.isPinned) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.PushPin, contentDescription = null,
                        tint = RSColors.AccentWarm, modifier = Modifier.size(10.dp))
                }
            }
            Row {
                if (isActive) {
                    // Pin toggle
                    IconButton(onClick = onPinToggle, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.PushPin, null,
                            tint = if (note.isPinned) RSColors.AccentWarm else RSColors.FaintText,
                            modifier = Modifier.size(14.dp))
                    }
                    // Delete
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.DeleteOutline, null,
                            tint = RSColors.FaintText, modifier = Modifier.size(14.dp))
                    }
                    // Close
                    IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, null,
                            tint = RSColors.FaintText, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Main text editor — URL detection happens automatically via ViewModel
        BasicTextField(
            value = note.content,
            onValueChange = onContentChange,
            textStyle = TextStyle(
                fontSize = 13.sp, fontWeight = FontWeight.Normal,
                color = RSColors.BodyText, lineHeight = 19.sp
            ),
            cursorBrush = SolidColor(RSColors.InkBlack),
            modifier = Modifier.fillMaxWidth()
        ) { innerTextField ->
            Box {
                if (note.content.isBlank()) {
                    Text(
                        text = "Write here… URLs are captured automatically.",
                        fontSize = 13.sp, color = RSColors.FaintText
                    )
                }
                innerTextField()
            }
        }

        // In-context Artifact Cards — rendered within the note flow
        if (note.links.isNotEmpty() && note.isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))

            // Divider
            Box(
                modifier = Modifier.fillMaxWidth().height(0.5.dp)
                    .background(RSColors.SubtleBorder)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // "Sources" label — mono-spaced
            Text(
                text = "SOURCES",
                fontSize = 8.sp, fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                color = RSColors.MonoText, letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            note.links.forEach { link ->
                ArtifactCard(metadata = link)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        // Expand/Collapse toggle — only when links exist
        if (note.links.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    onClick = onExpandToggle,
                    shape = RoundedCornerShape(4.dp),
                    color = RSColors.InkBlack.copy(alpha = 0.03f)
                ) {
                    Text(
                        text = if (note.isExpanded) "collapse" else "${note.links.size} source${if (note.links.size > 1) "s" else ""}",
                        fontSize = 9.sp, fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        color = RSColors.MonoText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Bottom spacing for source markers
        if (note.links.isNotEmpty()) {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
