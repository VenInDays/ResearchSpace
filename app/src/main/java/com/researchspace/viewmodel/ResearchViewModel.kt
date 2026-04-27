package com.researchspace.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.researchspace.data.LinkMetadata
import com.researchspace.data.NoteRepository
import com.researchspace.data.ResearchNote
import com.researchspace.networking.OpenGraphParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel for the Research Space screen.
 * Manages the spatial canvas state, note CRUD, and link metadata fetching.
 */
class ResearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NoteRepository(File(application.filesDir, "notes"))
    private val ogParser = OpenGraphParser()

    private val _notes = MutableStateFlow<List<ResearchNote>>(emptyList())
    val notes: StateFlow<List<ResearchNote>> = _notes.asStateFlow()

    private val _activeNoteId = MutableStateFlow<String?>(null)
    val activeNoteId: StateFlow<String?> = _activeNoteId.asStateFlow()

    private val _isEditorOpen = MutableStateFlow(false)
    val isEditorOpen: StateFlow<Boolean> = _isEditorOpen.asStateFlow()

    private val _urlInput = MutableStateFlow("")
    val urlInput: StateFlow<String> = _urlInput.asStateFlow()

    private val _isLoadingLink = MutableStateFlow(false)
    val isLoadingLink: StateFlow<Boolean> = _isLoadingLink.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _notes.value = repository.loadNotes()
        }
    }

    fun createNote(x: Float = 100f, y: Float = 100f) {
        viewModelScope.launch {
            val note = ResearchNote.createAt(
                x = x + _notes.value.size * 30f,
                y = y + _notes.value.size * 30f
            )
            val updatedNotes = _notes.value + note
            _notes.value = updatedNotes
            _activeNoteId.value = note.id
            _isEditorOpen.value = true
            saveNotes()
        }
    }

    fun updateNoteContent(noteId: String, content: String) {
        val updatedNotes = _notes.value.map { note ->
            if (note.id == noteId) note.copy(content = content) else note
        }
        _notes.value = updatedNotes
        viewModelScope.launch { saveNotes() }
    }

    fun selectNote(note: ResearchNote) {
        _activeNoteId.value = note.id
        _isEditorOpen.value = true
    }

    fun deselectNote() {
        _activeNoteId.value = null
        _isEditorOpen.value = false
        viewModelScope.launch { saveNotes() }
    }

    fun deleteNote(noteId: String) {
        val updatedNotes = _notes.value.filter { it.id != noteId }
        _notes.value = updatedNotes
        if (_activeNoteId.value == noteId) {
            _activeNoteId.value = null
            _isEditorOpen.value = false
        }
        viewModelScope.launch { saveNotes() }
    }

    fun togglePin(noteId: String) {
        val updatedNotes = _notes.value.map { note ->
            if (note.id == noteId) note.copy(isPinned = !note.isPinned) else note
        }
        _notes.value = updatedNotes
        viewModelScope.launch { saveNotes() }
    }

    fun dragNote(noteId: String, dx: Float, dy: Float) {
        val updatedNotes = _notes.value.map { note ->
            if (note.id == noteId) {
                note.copy(
                    positionX = note.positionX + dx,
                    positionY = note.positionY + dy
                )
            } else note
        }
        _notes.value = updatedNotes
        viewModelScope.launch { saveNotes() }
    }

    fun fetchLinkMetadata(url: String) {
        if (url.isBlank()) return
        val activeId = _activeNoteId.value ?: return

        viewModelScope.launch {
            _isLoadingLink.value = true
            try {
                val metadata = ogParser.parse(url)
                val updatedNotes = _notes.value.map { note ->
                    if (note.id == activeId) {
                        note.apply { addLink(metadata) }
                    } else note
                }
                _notes.value = updatedNotes
                _urlInput.value = ""
                _toastMessage.value = "Link captured: ${metadata.title.ifBlank { metadata.domain }}"
                saveNotes()
            } catch (e: Exception) {
                _toastMessage.value = "Failed to fetch link metadata"
            } finally {
                _isLoadingLink.value = false
            }
        }
    }

    fun removeLink(noteId: String, linkId: String) {
        val updatedNotes = _notes.value.map { note ->
            if (note.id == noteId) {
                note.apply { removeLink(linkId) }
            } else note
        }
        _notes.value = updatedNotes
        viewModelScope.launch { saveNotes() }
    }

    fun setUrlInput(value: String) {
        _urlInput.value = value
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun bringToFront(noteId: String) {
        val maxZ = _notes.value.maxOfOrNull { it.zIndex } ?: 0
        val updatedNotes = _notes.value.map { note ->
            if (note.id == noteId) note.copy(zIndex = maxZ + 1) else note
        }
        _notes.value = updatedNotes
    }

    private suspend fun saveNotes() {
        repository.saveNotes(_notes.value)
    }
}
