package com.jnetai.bookwriter.ui.book

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jnetai.bookwriter.R
import com.jnetai.bookwriter.data.entity.WorldbuildingNote

class WorldbuildingAdapter(private val onClick: (WorldbuildingNote) -> Unit) :
    ListAdapter<WorldbuildingNote, WorldbuildingAdapter.WorldViewHolder>(WorldDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorldViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_worldbuilding, parent, false)
        return WorldViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: WorldViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class WorldViewHolder(itemView: View, private val onClick: (WorldbuildingNote) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvWorldTitle)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvWorldCategory)
        private var current: WorldbuildingNote? = null

        init { itemView.setOnClickListener { current?.let { onClick(it) } } }

        fun bind(note: WorldbuildingNote) {
            current = note
            tvTitle.text = note.title
            tvCategory.text = note.category.ifEmpty { "General" }
        }
    }
}

class WorldDiffCallback : DiffUtil.ItemCallback<WorldbuildingNote>() {
    override fun areItemsTheSame(old: WorldbuildingNote, new: WorldbuildingNote) = old.id == new.id
    override fun areContentsTheSame(old: WorldbuildingNote, new: WorldbuildingNote) = old == new
}