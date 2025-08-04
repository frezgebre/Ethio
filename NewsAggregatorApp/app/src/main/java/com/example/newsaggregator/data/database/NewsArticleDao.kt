package com.example.newsaggregator.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsaggregator.data.NewsArticle

@Dao
interface NewsArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<NewsArticle>)

    @Query("SELECT * FROM news_articles ORDER BY pubDate DESC")
    fun getAllArticles(): kotlinx.coroutines.flow.Flow<List<NewsArticle>>

    @Query("DELETE FROM news_articles WHERE source = :sourceName")
    suspend fun deleteArticlesBySource(sourceName: String)

    @Query("DELETE FROM news_articles")
    suspend fun deleteAllArticles()
}


