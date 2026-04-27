package com.researchspace.data

import java.util.UUID

/**
 * Holds parsed OpenGraph metadata from a pasted URL.
 * Used to render Visual Link Cards as "Visual Artifacts" on the canvas.
 */
data class LinkMetadata(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val siteName: String = "",
    val type: String = "",
    val faviconUrl: String = ""
) {
    val domain: String
        get() {
            return try {
                val uri = android.net.Uri.parse(url)
                uri.host ?: url
            } catch (e: Exception) {
                url
            }
        }

    val hasImage: Boolean
        get() = imageUrl.isNotBlank()

    val isComplete: Boolean
        get() = title.isNotBlank() || description.isNotBlank()

    companion object {
        fun empty(url: String) = LinkMetadata(url = url)
    }
}
