
package com.example.newsaggregator.data

import android.util.Log
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RssFeedParser(private val rssParser: RssParser) {

    suspend fun parseFeed(url: String, sourceName: String): List<NewsArticle> = withContext(Dispatchers.IO) {
        try {
            val channel = rssParser.parse(url)
            channel.items?.mapNotNull { rssItem ->
                rssItem.toNewsArticle(sourceName)
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e("RssFeedParser", "Error parsing feed from $url", e)
            emptyList()
        }
    }

    private fun RssItem.toNewsArticle(sourceName: String): NewsArticle? {
        return if (link != null && title != null && pubDateMillis != null) {
            NewsArticle(
                title = title,
                link = link,
                description = description,
                imageUrl = image,
                pubDate = pubDateMillis,
                source = sourceName
            )
        } else {
            null
        }
    }
}


