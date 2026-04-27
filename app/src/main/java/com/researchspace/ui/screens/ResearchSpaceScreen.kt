package com.researchspace.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.researchspace.data.ResearchNote
import com.researchspace.ui.components.*
import com.researchspace.ui.theme.RSColors
import com.researchspace.viewmodel.ResearchViewModel

/**
 * Main Research Space screen composable.
 * Composes the spatial canvas, floating toolbar, orbital hub, and note editor.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ResearchSpaceScreen(
    viewModel: ResearchViewModel
) {
    val notes by viewModel.notes.collectAsState()
    val activeNoteId by viewModel.activeNoteId.collectAsState()
    val isEditorOpen by viewModel.isEditorOpen.collectAsState()
    val urlInput by viewModel.urlInput.collectAsState()
    val isLoadingLink by viewModel.isLoadingLink.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val context = LocalContext.current

    // Show toasts
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Monitor clipboard for URL paste
    val clipboardManager = LocalClipboardManager.current
    LaunchedEffect(isEditorOpen) {
        if (isEditorOpen) {
            clipboardManager.getText()?.text?.let { text ->
                if (text.matches(Regex("^https?://\\S+$"))) {
                    viewModel.setUrlInput(text)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RSColors.OffWhite)
    ) {
        // Spatial Canvas (background layer)
        SpatialCanvas(
            notes = notes,
            activeNoteId = activeNoteId,
            onNoteClick = { note ->
                viewModel.selectNote(note)
                viewModel.bringToFront(note.id)
            },
            onNoteDrag = { id, dx, dy ->
                viewModel.dragNote(id, dx, dy)
            },
            onCanvasTap = { offset ->
                viewModel.deselectNote()
            },
            noteContent = { note ->
                NoteContentPreview(note = note, onRemoveLink = { linkId ->
                    viewModel.removeLink(note.id, linkId)
                })
            },
            modifier = Modifier.fillMaxSize()
        )

        // Empty state
        if (notes.isEmpty()) {
            EmptyCanvasState(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Floating Toolbar (overlay — visible when editor is open)
        FloatingToolbar(
            onAction = { action ->
                // Handle toolbar actions
                when (action) {
                    ToolbarAction.LINK -> {
                        // Focus on URL input
                    }
                    ToolbarAction.BOLD, ToolbarAction.ITALIC,
                    ToolbarAction.UNDERLINE, ToolbarAction.CODE,
                    ToolbarAction.CHECKLIST, ToolbarAction.IMAGE,
                    ToolbarAction.MORE -> {
                        Toast.makeText(context, "Format: $action", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            visible = isEditorOpen,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Note Editor Panel (slide-up overlay)
        AnimatedVisibility(
            visible = isEditorOpen,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(dampingRatio = 0.85f, stiffness = 300)
            ) + fadeIn(animationSpec = tween(200)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = spring(dampingRatio = 0.9f, stiffness = 400)
            ) + fadeOut(animationSpec = tween(150)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            val activeNote = notes.find { it.id == activeNoteId }
            if (activeNote != null) {
                NoteEditorPanel(
                    note = activeNote,
                    urlInput = urlInput,
                    isLoadingLink = isLoadingLink,
                    onContentChange = { viewModel.updateNoteContent(activeNote.id, it) },
                    onUrlChange = { viewModel.setUrlInput(it) },
                    onFetchLink = { viewModel.fetchLinkMetadata(urlInput) },
                    onPinToggle = { viewModel.togglePin(activeNote.id) },
                    onDelete = { viewModel.deleteNote(activeNote.id) },
                    onClose = { viewModel.deselectNote() },
                    onRemoveLink = { viewModel.removeLink(activeNote.id, it) }
                )
            }
        }

        // Orbital Hub (always visible, bottom-center)
        OrbitalHub(
            onAction = { action ->
                when (action) {
                    OrbitalAction.ADD_NOTE -> viewModel.createNote()
                    OrbitalAction.SEARCH -> {
                        Toast.makeText(context, "Search coming soon", Toast.LENGTH_SHORT).show()
                    }
                    OrbitalAction.BOOKMARKS -> {
                        Toast.makeText(context, "Bookmarks coming soon", Toast.LENGTH_SHORT).show()
                    }
                    OrbitalAction.SETTINGS -> {
                        Toast.makeText(context, "Settings coming soon", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * Note editor panel — slides up from the bottom.
 * Contains the note text editor, link input, and link cards.
 */
@Composable
private fun NoteEditorPanel(
    note: ResearchNote,
    urlInput: String,
    isLoadingLink: Boolean,
    onContentChange: (String) -> Unit,
    onUrlChange: (String) -> Unit,
    onFetchLink: () -> Unit,
    onPinToggle: () -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit,
    onRemoveLink: (String) -> Unit
) {
    val panelShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.65f)
            .shadow(
                elevation = 32.dp,
                shape = panelShape,
                ambientColor = RSColors.ShadowDark.copy(alpha = 0.25f),
                spotColor = RSColors.ShadowDark.copy(alpha = 0.1f)
            )
            .clip(panelShape)
            .background(RSColors.OffWhite)
            .border(
                width = 0.5.dp,
                color = RSColors.SubtleGrey,
                shape = panelShape
            )
    ) {
        // Handle bar
        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .size(width = 40.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(RSColors.SubtleGrey)
                .align(Alignment.CenterHorizontally)
        )

        // Editor header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Editing",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = RSColors.MutedText,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = note.formattedTimestamp,
                    fontSize = 11.sp,
                    color = RSColors.SubtleGrey
                )
            }
            Row {
                IconButton(onClick = onPinToggle, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pin",
                        tint = if (note.isPinned) RSColors.AccentWarm else RSColors.MutedText,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = RSColors.MutedText,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onClose, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = RSColors.MutedText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp),
            thickness = 0.5.dp,
            color = RSColors.SubtleGrey
        )

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Text editor
            BasicTextField(
                value = note.content,
                onValueChange = onContentChange,
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = RSColors.InkBlack,
                    lineHeight = 22.sp
                ),
                cursorBrush = SolidColor(RSColors.InkBlack),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 100.dp)
            ) { innerTextField ->
                Box {
                    if (note.content.isBlank()) {
                        Text(
                            text = "Start writing your research notes…",
                            fontSize = 15.sp,
                            color = RSColors.SubtleGrey
                        )
                    }
                    innerTextField()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // URL input for link capture
            LinkInputField(
                value = urlInput,
                onValueChange = onUrlChange,
                onSubmit = onFetchLink
            )

            // Loading indicator
            if (isLoadingLink) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp)),
                    color = RSColors.InkBlack,
                    trackColor = RSColors.SubtleGrey
                )
            }

            // Visual link cards
            if (note.links.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Captured Links",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = RSColors.MutedText,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                note.links.forEach { link ->
                    Box {
                        VisualLinkCard(
                            metadata = link,
                            onClick = { /* Open in browser */ }
                        )
                        // Remove button
                        IconButton(
                            onClick = { onRemoveLink(link.id) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(28.dp)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove link",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // Bottom padding for safe area
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Compact note content preview for the spatial canvas.
 */
@Composable
private fun NoteContentPreview(
    note: ResearchNote,
    onRemoveLink: (String) -> Unit
) {
    Column {
        // Use default note content
        DefaultNoteContent(note)
    }
}
