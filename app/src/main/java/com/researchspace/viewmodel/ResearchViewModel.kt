package com.researchspace.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.researchspace.data.LinkMetadata
import com.researchspace.data.NoteRepository
import com.researchspace.data.ResearchNote
import com.researchspace.networking.OpenGraphParser
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class ResearchViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NoteRepository(File(application.filesDir, "notes"))
    private val ogParser = OpenGraphParser()

    private val _notes = MutableStateFlow<List<ResearchNote>>(emptyList())
    val notes: StateFlow<List<ResearchNote>> = _notes.asStateFlow()

    private val _activeNoteId = MutableStateFlow<String?>(null)
    val activeNoteId: StateFlow<String?> = _activeNoteId.asStateFlow()

    private val _focusedSourceId = MutableStateFlow<String?>(null)
    val focusedSourceId: StateFlow<String?> = _focusedSourceId.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Track URLs we've already fetched to avoid duplicates
    private val fetchedUrls = mutableMapOf<String, LinkMetadata>()
    private var fetchJob: Job? = null

    init { loadNotes() }

    private fun loadNotes() {
        viewModelScope.launch {
            _notes.value = repository.loadNotes()
            // Rebuild fetched URLs cache from existing links
            _notes.value.forEach { note ->
                note.links.forEach { link -> fetchedUrls[link.url] = link }
            }
        }
    }

    fun createNote(x: Float = 100f, y: Float = 100f) {
        viewModelScope.launch {
            val note = ResearchNote.createAt(x, y, _notes.value.size)
            _notes.value = _notes.value + note
            _activeNoteId.value = note.id
            saveNotes()
        }
    }

    fun updateNoteContent(noteId: String, content: String) {
        val updated = _notes.value.map { if (it.id == noteId) it.copy(content = content) else it }
        _notes.value = updated
        // Auto-detect URLs in content (non-blocking)
        detectAndFetchUrls(noteId, content)
        viewModelScope.launch { saveNotes() }
    }

    private fun detectAndFetchUrls(noteId: String, content: String) {
        val urlPattern = Regex("""https?://[^\s<>"{}|\\^`\[\]]+""")
        val urls = urlPattern.findAll(content).map { it.value }.distinct().toList()

        for (url in urls) {
            if (fetchedUrls.containsKey(url)) continue
            // Mark as in-progress to avoid re-fetching
            fetchedUrls[url] = LinkMetadata.empty(url)
            fetchUrlMetadata(noteId, url)
        }
    }

    private fun fetchUrlMetadata(noteId: String, url: String) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val metadata = ogParser.parse(url)
                fetchedUrls[url] = metadata
                val updated = _notes.value.map { note ->
                    if (note.id == noteId) {
                        // Replace the empty placeholder or add new
                        val existing = note.links.find { it.url == url }
                        if (existing != null && !existing.isComplete) {
                            note.copy(links = note.links.map { if (it.url == url) metadata else it })
                        } else if (existing == null) {
                            note.apply { addLink(metadata) }
                        } else note
                    } else note
                }
                _notes.value = updated
                _toastMessage.value = "Source captured"
                saveNotes()
            } catch (_: Exception) { /* silent fail — don't interrupt typing */ }
        }
    }

    fun selectNote(note: ResearchNote) {
        _activeNoteId.value = note.id
        bringToFront(note.id)
    }

    fun deselectNote() {
        _activeNoteId.value = null
        _focusedSourceId.value = null
    }

    fun deleteNote(noteId: String) {
        _notes.value = _notes.value.filter { it.id != noteId }
        if (_activeNoteId.value == noteId) {
            _activeNoteId.value = null
            _focusedSourceId.value = null
        }
        viewModelScope.launch { saveNotes() }
    }

    fun togglePin(noteId: String) {
        _notes.value = _notes.value.map { if (it.id == noteId) it.copy(isPinned = !it.isPinned) else it }
        viewModelScope.launch { saveNotes() }
    }

    fun dragNote(noteId: String, dx: Float, dy: Float) {
        _notes.value = _notes.value.map {
            if (it.id == noteId) it.copy(positionX = it.positionX + dx, positionY = it.positionY + dy) else it
        }
        viewModelScope.launch { saveNotes() }
    }

    fun toggleExpand(noteId: String) {
        _notes.value = _notes.value.map { if (it.id == noteId) it.copy(isExpanded = !it.isExpanded) else it }
        viewModelScope.launch { saveNotes() }
    }

    fun focusSource(linkId: String) {
        _focusedSourceId.value = linkId
    }

    fun clearSourceFocus() {
        _focusedSourceId.value = null
    }

    fun removeLink(noteId: String, linkId: String) {
        _notes.value = _notes.value.map {
            if (it.id == noteId) it.apply { removeLink(linkId) } else it
        }
        viewModelScope.launch { saveNotes() }
    }

    fun clearToast() { _toastMessage.value = null }

    fun bringToFront(noteId: String) {
        val maxZ = _notes.value.maxOfOrNull { it.zIndex } ?: 0
        _notes.value = _notes.value.map { if (it.id == noteId) it.copy(zIndex = maxZ + 1) else it }
    }

    private suspend fun saveNotes() { repository.saveNotes(_notes.value) }
}
