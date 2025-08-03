package com.example.newsaggregator.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_articles")
data class NewsArticle(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val link: String,
    val description: String?,
    val imageUrl: String?,
    val pubDate: Long,
    val source: String
)


