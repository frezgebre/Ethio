
package com.example.newsaggregator.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newsaggregator.data.NewsArticle
import com.example.newsaggregator.databinding.ItemNewsArticleBinding

class NewsAdapter(private val onItemClick: (NewsArticle) -> Unit) :
    ListAdapter<NewsArticle, NewsAdapter.NewsViewHolder>(NewsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NewsViewHolder(private val binding: ItemNewsArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: NewsArticle) {
            binding.textTitle.text = article.title
            binding.textDescription.text = article.description ?: ""
            binding.textSource.text = article.source
            binding.textTime.text = DateUtils.getRelativeTimeSpanString(
                article.pubDate,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
            )

            if (article.imageUrl != null) {
                binding.imageThumbnail.visibility = android.view.View.VISIBLE
                com.bumptech.glide.Glide.with(binding.root)
                    .load(article.imageUrl)
                    .into(binding.imageThumbnail)
            } else {
                binding.imageThumbnail.visibility = android.view.View.GONE
            }

            binding.root.setOnClickListener {
                onItemClick(article)
            }
        }
    }

    class NewsDiffCallback : DiffUtil.ItemCallback<NewsArticle>() {
        override fun areItemsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
            return oldItem.link == newItem.link
        }

        override fun areContentsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
            return oldItem == newItem
        }
    }
}

