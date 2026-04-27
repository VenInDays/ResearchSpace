package com.researchspace.data

import java.util.UUID
import java.util.concurrent.TimeUnit

data class ResearchNote(
    val id: String = UUID.randomUUID().toString(),
    var content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    var links: List<LinkMetadata> = emptyList(),
    var positionX: Float = 0f,
    var positionY: Float = 0f,
    var width: Float = 320f,
    var height: Float = 240f,
    var isPinned: Boolean = false,
    var zIndex: Int = 0,
    var isExpanded: Boolean = false
) {
    val formattedTimestamp: String
        get() {
            val diff = System.currentTimeMillis() - timestamp
            return when {
                diff < TimeUnit.MINUTES.toMillis(1) -> "just now"
                diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m"
                diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h"
                else -> "${TimeUnit.MILLISECONDS.toDays(diff)}d"
            }
        }

    val displayHeight: Float
        get() = if (isExpanded) height * 2.2f else height

    fun addLink(link: LinkMetadata) { links = links + link }
    fun removeLink(linkId: String) { links = links.filter { it.id != linkId } }

    companion object {
        fun createAt(x: Float, y: Float, index: Int) = ResearchNote(
            positionX = x + index * 40f,
            positionY = y + index * 35f
        )
    }
}
