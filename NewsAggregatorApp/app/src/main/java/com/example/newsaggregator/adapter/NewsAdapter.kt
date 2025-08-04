
package com.example.newsaggregator.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

            Glide.with(binding.imageThumbnail.context)
                .load(article.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(binding.imageThumbnail)

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

