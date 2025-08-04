package com.example.newsaggregator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsaggregator.data.NewsArticle
import com.example.newsaggregator.data.NewsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    private val allArticlesFlow: StateFlow<List<NewsArticle>> = newsRepository.getArticles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val articles: StateFlow<List<NewsArticle>> = _searchQuery
        .debounce(300)
        .combine(allArticlesFlow) { query, articles ->
            if (query.isBlank()) {
                articles
            } else {
                articles.filter { article ->
                    article.title.contains(query, ignoreCase = true) ||
                    article.description?.contains(query, ignoreCase = true) == true ||
                    article.source.contains(query, ignoreCase = true)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    init {
        refreshNews()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun refreshNews() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                newsRepository.fetchAndCacheArticles()
            } catch (e: Exception) {
                _error.emit("Failed to fetch news. Please check your connection.")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
