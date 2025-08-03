
package com.example.newsaggregator.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newsaggregator.data.NewsArticle

@Database(entities = [NewsArticle::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsArticleDao(): NewsArticleDao
}


