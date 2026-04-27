package com.researchspace.networking

import com.researchspace.data.LinkMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.HttpURLConnection
import java.net.URL

/**
 * OpenGraph metadata parser.
 * Fetches a URL, parses its HTML, and extracts og:title, og:image, og:description,
 * and other relevant metadata from OpenGraph and standard meta tags.
 */
class OpenGraphParser {

    suspend fun parse(url: String): LinkMetadata = withContext(Dispatchers.IO) {
        try {
            val normalizedUrl = normalizeUrl(url)
            val connection = URL(normalizedUrl).openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 ResearchSpace/1.0"
            )
            connection.instanceFollowRedirects = true

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext LinkMetadata.empty(normalizedUrl)
            }

            val document: Document = Jsoup.parse(
                connection.inputStream,
                connection.contentEncoding ?: "UTF-8",
                normalizedUrl
            )

            // Extract OG meta tags
            val ogTitle = getMetaContent(document, "og:title")
                ?: document.title()
            val ogDescription = getMetaContent(document, "og:description")
                ?: getMetaContent(document, "description")
                ?: ""
            val ogImage = getMetaContent(document, "og:image")
                ?: getMetaContent(document, "og:image:url")
                ?: ""
            val ogSiteName = getMetaContent(document, "og:site_name")
                ?: extractDomain(normalizedUrl)
            val ogType = getMetaContent(document, "og:type") ?: "website"

            // Try to get favicon
            val favicon = getFavicon(document, normalizedUrl)

            LinkMetadata(
                url = normalizedUrl,
                title = ogTitle.trim(),
                description = ogDescription.trim(),
                imageUrl = ogImage.trim(),
                siteName = ogSiteName.trim(),
                type = ogType.trim(),
                faviconUrl = favicon
            )
        } catch (e: Exception) {
            // If parsing fails, return empty metadata with the URL
            LinkMetadata.empty(normalizeUrl(url))
        }
    }

    private fun getMetaContent(doc: Document, property: String): String? {
        // Try property attribute first (standard OG)
        val byProperty = doc.selectFirst("meta[property=\"$property\"]")
            ?.attr("content")
        if (!byProperty.isNullOrBlank()) return byProperty

        // Try name attribute (Twitter cards, older meta)
        val byName = doc.selectFirst("meta[name=\"$property\"]")
            ?.attr("content")
        return byName
    }

    private fun getFavicon(doc: Document, baseUrl: String): String {
        // Check <link rel="icon">
        val iconLink = doc.selectFirst("link[rel~=\"icon\"]")
        val href = iconLink?.attr("abs:href")
        if (!href.isNullOrBlank()) return href

        // Fallback: /favicon.ico
        return "$baseUrl/favicon.ico"
    }

    private fun normalizeUrl(url: String): String {
        return when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            else -> "https://$url"
        }
    }

    private fun extractDomain(url: String): String {
        return try {
            val uri = java.net.URI(url)
            uri.host ?: url
        } catch (e: Exception) {
            url
        }
    }
}
