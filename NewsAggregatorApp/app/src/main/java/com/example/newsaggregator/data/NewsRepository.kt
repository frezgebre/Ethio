
package com.example.newsaggregator.data

import android.content.Context
import androidx.room.Room
import com.example.newsaggregator.data.database.AppDatabase
import com.prof18.rssparser.RssParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsRepository(private val context: Context) {

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "news-db"
        ).build()
    }

    private val newsArticleDao = database.newsArticleDao()
    private val rssParser = RssFeedParser(RssParser())

    private val rssFeedUrls = mapOf(
        "VOA Amharic" to "https://amharic.voanews.com/api/zy--yl-vomx-tpetyqqv",
        "Addis Standard" to "https://addisstandard.com/feed/",
        "Aiga News" to "https://aiganews.com/feed",
        "Ethiopia Insight" to "https://ethiopia-insight.com/feed",
        "New Business Ethiopia" to "https://newbusinessethiopia.com/feed",
        "Maleda Times" to "https://maledatimes.com/feed",
        "Ethiopia Nege" to "https://ethiopianege.com/feed",
        "Debteraw" to "https://debteraw.com/feed"
    )

    suspend fun fetchAndCacheArticles(selectedSources: Set<String>) {
        withContext(Dispatchers.IO) {
            rssFeedUrls.filter { selectedSources.contains(it.key) }
                .forEach { (sourceName, url) ->
                    try {
                        val articles = rssParser.parseFeed(url, sourceName)
                        newsArticleDao.deleteArticlesBySource(sourceName)
                        newsArticleDao.insertAll(articles)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }

    suspend fun getArticles(selectedSources: Set<String>): List<NewsArticle> {
        return newsArticleDao.getArticlesBySource(selectedSources.toList())
    }

    fun getSourceNames(): List<String> {
        return rssFeedUrls.keys.toList()
    }
}


