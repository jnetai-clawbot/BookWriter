package com.jnetai.bookwriter.ui.book

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jnetai.bookwriter.R
import com.jnetai.bookwriter.data.entity.Chapter

class ChapterAdapter(private val onClick: (Chapter) -> Unit) :
    ListAdapter<Chapter, ChapterAdapter.ChapterViewHolder>(ChapterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chapter, parent, false)
        return ChapterViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ChapterViewHolder(itemView: View, private val onClick: (Chapter) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvChapterTitle)
        private val tvWords: TextView = itemView.findViewById(R.id.tvChapterWords)
        private var current: Chapter? = null

        init { itemView.setOnClickListener { current?.let { onClick(it) } } }

        fun bind(chapter: Chapter) {
            current = chapter
            tvTitle.text = chapter.title
            val words = if (chapter.content.isEmpty()) 0 else chapter.content.split("\\s+".toRegex()).size
            tvWords.text = "$words words"
        }
    }
}

class ChapterDiffCallback : DiffUtil.ItemCallback<Chapter>() {
    override fun areItemsTheSame(old: Chapter, new: Chapter) = old.id == new.id
    override fun areContentsTheSame(old: Chapter, new: Chapter) = old == new
}