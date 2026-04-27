package com.researchspace.data

import java.util.UUID

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
        get() = try { java.net.URI(url).host ?: url } catch (_: Exception) { url }

    val hasImage: Boolean get() = imageUrl.isNotBlank()
    val isComplete: Boolean get() = title.isNotBlank() || description.isNotBlank()

    val shortUrl: String
        get() = if (url.length > 35) url.take(32) + "..." else url

    companion object {
        fun empty(url: String) = LinkMetadata(url = url)
    }
}
