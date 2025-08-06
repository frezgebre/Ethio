package com.example.newsaggregator.data

import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssChannel
import com.prof18.rssparser.model.RssItem
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.lang.Exception

class RssFeedParserTest {

    private lateinit var rssParser: RssParser
    private lateinit var rssFeedParser: RssFeedParser

    @Before
    fun setUp() {
        rssParser = mock(RssParser::class.java)
        rssFeedParser = RssFeedParser(rssParser)
    }

    @Test
    fun `parseFeed returns list of news articles on successful parsing`() = runBlocking {
        val rssChannel = RssChannel(
            title = "Test Feed",
            link = "http://test.com",
            description = "Test Description",
            items = listOf(
                RssItem(
                    title = "Test Article 1",
                    link = "http://test.com/1",
                    description = "Description 1",
                    pubDateMillis = 1672531200000 // 2023-01-01
                ),
                RssItem(
                    title = "Test Article 2",
                    link = "http://test.com/2",
                    description = "Description 2",
                    pubDateMillis = 1672617600000 // 2023-01-02
                )
            )
        )

        `when`(rssParser.parse("http://test.com/feed")).thenReturn(rssChannel)

        val articles = rssFeedParser.parseFeed("http://test.com/feed", "Test Source")

        assertEquals(2, articles.size)
        assertEquals("Test Article 1", articles[0].title)
        assertEquals("Test Source", articles[0].source)
    }

    @Test
    fun `parseFeed returns empty list on parsing error`() = runBlocking {
        `when`(rssParser.parse("http://invalid.com/feed")).thenThrow(Exception("Parsing error"))

        val articles = rssFeedParser.parseFeed("http://invalid.com/feed", "Invalid Source")

        assertEquals(0, articles.size)
    }

    @Test
    fun `toNewsArticle returns null if link is null`() {
        val rssItem = RssItem(title = "Test Title", link = null, pubDateMillis = 1672531200000)
        val newsArticle = rssItem.toNewsArticle("Test Source")
        assertNull(newsArticle)
    }

    @Test
    fun `toNewsArticle returns null if title is null`() {
        val rssItem = RssItem(title = null, link = "http://test.com", pubDateMillis = 1672531200000)
        val newsArticle = rssItem.toNewsArticle("Test Source")
        assertNull(newsArticle)
    }

    @Test
    fun `toNewsArticle returns null if pubDateMillis is null`() {
        val rssItem = RssItem(title = "Test Title", link = "http://test.com", pubDateMillis = null)
        val newsArticle = rssItem.toNewsArticle("Test Source")
        assertNull(newsArticle)
    }

    @Test
    fun `toNewsArticle returns NewsArticle when all fields are valid`() {
        val rssItem = RssItem(
            title = "Test Title",
            link = "http://test.com",
            description = "Test Description",
            image = "http://test.com/image.jpg",
            pubDateMillis = 1672531200000
        )
        val newsArticle = rssItem.toNewsArticle("Test Source")
        assertNotNull(newsArticle)
        assertEquals("Test Title", newsArticle?.title)
        assertEquals("http://test.com", newsArticle?.link)
        assertEquals("Test Description", newsArticle?.description)
        assertEquals("http://test.com/image.jpg", newsArticle?.imageUrl)
        assertEquals(1672531200000, newsArticle?.pubDate)
        assertEquals("Test Source", newsArticle?.source)
    }
}
