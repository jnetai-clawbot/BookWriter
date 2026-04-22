package com.jnetai.bookwriter.ui.book

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jnetai.bookwriter.R
import com.jnetai.bookwriter.data.entity.Book

class BookAdapter(private val onClick: (Book) -> Unit) :
    ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BookViewHolder(itemView: View, private val onClick: (Book) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvBookTitle)
        private val tvGenre: TextView = itemView.findViewById(R.id.tvBookGenre)
        private val tvProgress: TextView = itemView.findViewById(R.id.tvBookProgress)
        private var currentBook: Book? = null

        init {
            itemView.setOnClickListener { currentBook?.let { onClick(it) } }
        }

        fun bind(book: Book) {
            currentBook = book
            tvTitle.text = book.title
            tvGenre.text = book.genre.ifEmpty { "No genre" }
            tvProgress.text = "${book.targetWordCount} words target"
        }
    }
}

class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Book, newItem: Book) = oldItem == newItem
}