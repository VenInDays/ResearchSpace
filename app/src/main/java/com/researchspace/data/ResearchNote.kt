package com.researchspace.data

import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Represents a single research note on the spatial canvas.
 * Each note can contain rich text content and an associated list of
 * link metadata artifacts collected from pasted URLs.
 */
data class ResearchNote(
    val id: String = UUID.randomUUID().toString(),
    var content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    var links: List<LinkMetadata> = emptyList(),
    // Spatial position on the 2D canvas
    var positionX: Float = 0f,
    var positionY: Float = 0f,
    var width: Float = 320f,
    var height: Float = 240f,
    var isPinned: Boolean = false,
    var zIndex: Int = 0
) {
    val formattedTimestamp: String
        get() {
            val now = System.currentTimeMillis()
            val diff = now - timestamp
            return when {
                diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
                diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m ago"
                diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
                else -> "${TimeUnit.MILLISECONDS.toDays(diff)}d ago"
            }
        }

    fun addLink(link: LinkMetadata) {
        links = links + link
    }

    fun removeLink(linkId: String) {
        links = links.filter { it.id != linkId }
    }

    companion object {
        fun createAt(x: Float, y: Float): ResearchNote {
            return ResearchNote(positionX = x, positionY = y)
        }
    }
}
