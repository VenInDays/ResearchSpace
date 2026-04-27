package com.researchspace.networking

import com.researchspace.data.LinkMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL

class OpenGraphParser {

    suspend fun parse(url: String): LinkMetadata = withContext(Dispatchers.IO) {
        try {
            val normalized = normalizeUrl(url)
            val conn = URL(normalized).openConnection() as HttpURLConnection
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 ResearchSpace/2.0")
            conn.instanceFollowRedirects = true
            if (conn.responseCode != HttpURLConnection.HTTP_OK) return@withContext LinkMetadata.empty(normalized)

            val doc = Jsoup.parse(conn.inputStream, conn.contentEncoding ?: "UTF-8", normalized)
            LinkMetadata(
                url = normalized,
                title = getMeta(doc, "og:title") ?: doc.title().orEmpty(),
                description = getMeta(doc, "og:description") ?: getMeta(doc, "description").orEmpty(),
                imageUrl = getMeta(doc, "og:image") ?: getMeta(doc, "og:image:url").orEmpty(),
                siteName = getMeta(doc, "og:site_name") ?: extractDomain(normalized),
                type = getMeta(doc, "og:type").orEmpty()
            )
        } catch (_: Exception) { LinkMetadata.empty(normalizeUrl(url)) }
    }

    private fun getMeta(doc: org.jsoup.nodes.Document, property: String): String? {
        return doc.selectFirst("meta[property=\"$property\"]")?.attr("content")?.takeIf { it.isNotBlank() }
            ?: doc.selectFirst("meta[name=\"$property\"]")?.attr("content")?.takeIf { it.isNotBlank() }
    }

    private fun normalizeUrl(url: String) = when {
        url.startsWith("http://") || url.startsWith("https://") -> url
        else -> "https://$url"
    }

    private fun extractDomain(url: String) = try { java.net.URI(url).host ?: url } catch (_: Exception) { url }
}
