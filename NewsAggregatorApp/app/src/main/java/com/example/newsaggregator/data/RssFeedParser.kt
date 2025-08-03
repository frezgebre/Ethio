
package com.example.newsaggregator.data

import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RssFeedParser(private val rssParser: RssParser) {

    suspend fun parseFeed(url: String, sourceName: String): List<NewsArticle> = withContext(Dispatchers.IO) {
        val channel = rssParser.parse(url)
        channel.items?.mapNotNull { rssItem ->
            rssItem.toNewsArticle(sourceName)
        } ?: emptyList()
    }

    private fun RssItem.toNewsArticle(sourceName: String): NewsArticle? {
        return if (link != null && title != null) {
            NewsArticle(
                title = title,
                link = link,
                description = description,
                imageUrl = image,
                pubDate = pubDateMillis ?: System.currentTimeMillis(),
                source = sourceName
            )
        } else {
            null
        }
    }
}


