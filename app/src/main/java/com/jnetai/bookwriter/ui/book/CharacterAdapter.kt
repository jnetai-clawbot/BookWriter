package com.jnetai.bookwriter.ui.book

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jnetai.bookwriter.R
import com.jnetai.bookwriter.data.entity.Character

class CharacterAdapter(private val onClick: (Character) -> Unit) :
    ListAdapter<Character, CharacterAdapter.CharacterViewHolder>(CharacterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_character, parent, false)
        return CharacterViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CharacterViewHolder(itemView: View, private val onClick: (Character) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvCharName)
        private val tvDesc: TextView = itemView.findViewById(R.id.tvCharDesc)
        private var current: Character? = null

        init { itemView.setOnClickListener { current?.let { onClick(it) } } }

        fun bind(character: Character) {
            current = character
            tvName.text = character.name
            tvDesc.text = character.description.ifEmpty { "No description" }
        }
    }
}

class CharacterDiffCallback : DiffUtil.ItemCallback<Character>() {
    override fun areItemsTheSame(old: Character, new: Character) = old.id == new.id
    override fun areContentsTheSame(old: Character, new: Character) = old == new
}