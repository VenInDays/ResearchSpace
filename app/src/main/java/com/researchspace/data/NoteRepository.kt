package com.researchspace.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Local file-based persistence for research notes.
 * Uses JSON serialization via Gson for simple, dependency-free storage.
 */
class NoteRepository(private val directory: File) {

    private val gson = Gson()
    private val notesFile = File(directory, "research_notes.json")

    suspend fun saveNotes(notes: List<ResearchNote>) = withContext(Dispatchers.IO) {
        if (!directory.exists()) directory.mkdirs()
        val json = gson.toJson(notes)
        notesFile.writeText(json)
    }

    suspend fun loadNotes(): List<ResearchNote> = withContext(Dispatchers.IO) {
        if (!notesFile.exists()) return@withContext emptyList()
        try {
            val json = notesFile.readText()
            val type = object : TypeToken<List<ResearchNote>>() {}.type
            gson.fromJson<List<ResearchNote>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
