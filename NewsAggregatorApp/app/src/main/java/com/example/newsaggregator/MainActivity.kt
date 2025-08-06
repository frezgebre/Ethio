package com.example.newsaggregator

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsaggregator.adapter.NewsAdapter
import com.example.newsaggregator.data.NewsArticle
import com.example.newsaggregator.data.NewsRepository
import com.example.newsaggregator.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsRepository: NewsRepository
    private lateinit var sharedPreferences: SharedPreferences
    private var allArticles: List<NewsArticle> = emptyList()
    private var filteredArticles: List<NewsArticle> = emptyList()
    private val searchHandler = android.os.Handler()
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSharedPreferences()
        setupRecyclerView()
        setupRepository()
        setupSwipeRefresh()
        
        // Load initial data
        binding.swipeRefreshLayout.post { binding.swipeRefreshLayout.isRefreshing = true }
        loadNews()
    }

    private fun setupSharedPreferences() {
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        
        // Apply saved theme
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        recreate()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        
        // Setup search functionality
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                searchRunnable = Runnable {
                    filterArticles(newText ?: "")
                }
                searchHandler.postDelayed(searchRunnable!!, 300)
                return true
            }
        })
        
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_theme -> {
                toggleTheme()
                true
            }
            R.id.action_filter -> {
                showSourceFilterDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleTheme() {
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        val newMode = !isDarkMode
        
        sharedPreferences.edit().putBoolean("dark_mode", newMode).apply()
        
        if (newMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun filterArticles(query: String) {
        filteredArticles = if (query.isEmpty()) {
            allArticles
        } else {
            allArticles.filter { article ->
                article.title.contains(query, ignoreCase = true) ||
                article.description?.contains(query, ignoreCase = true) == true ||
                article.source.contains(query, ignoreCase = true)
            }
        }
        newsAdapter.submitList(filteredArticles)
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter { article ->
            // Handle article click - open ArticleActivity
            val intent = Intent(this, ArticleActivity::class.java).apply {
                putExtra(ArticleActivity.EXTRA_ARTICLE_URL, article.link)
            }
            startActivity(intent)
        }
        
        binding.recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupRepository() {
        newsRepository = NewsRepository(this)
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadNews()
        }
    }

    private fun showSourceFilterDialog() {
        val sourceNames = newsRepository.getSourceNames().toTypedArray()
        val selectedSources = sharedPreferences.getStringSet("selected_sources", sourceNames.toSet()) ?: sourceNames.toSet()
        val checkedItems = sourceNames.map { selectedSources.contains(it) }.toBooleanArray()

        AlertDialog.Builder(this)
            .setTitle("Select News Sources")
            .setMultiChoiceItems(sourceNames, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("OK") { _, _ ->
                val newSelectedSources = sourceNames.filterIndexed { index, _ -> checkedItems[index] }.toSet()
                sharedPreferences.edit().putStringSet("selected_sources", newSelectedSources).apply()
                loadNews()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadNews() {
        binding.swipeRefreshLayout.isRefreshing = true
        
        lifecycleScope.launch {
            try {
                val selectedSources = sharedPreferences.getStringSet("selected_sources", newsRepository.getSourceNames().toSet()) ?: newsRepository.getSourceNames().toSet()
                // Fetch fresh articles from RSS feeds
                newsRepository.fetchAndCacheArticles(selectedSources)
                
                // Get sorted articles from database
                allArticles = newsRepository.getArticles(selectedSources)
                filteredArticles = allArticles
                
                // Update UI
                newsAdapter.submitList(filteredArticles)
            } catch (e: Exception) {
                e.printStackTrace()
                // Load cached articles if network fails
                val selectedSources = sharedPreferences.getStringSet("selected_sources", newsRepository.getSourceNames().toSet()) ?: newsRepository.getSourceNames().toSet()
                allArticles = newsRepository.getArticles(selectedSources)
                filteredArticles = allArticles
                newsAdapter.submitList(filteredArticles)
                Toast.makeText(this@MainActivity, "Failed to fetch fresh news. Displaying cached articles.", Toast.LENGTH_SHORT).show()
            } finally {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}

