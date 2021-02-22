package com.koc.touchnotes.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.koc.touchnotes.databinding.NoteListLayoutBinding
import com.koc.touchnotes.interfaces.ClickListener
import com.koc.touchnotes.model.Note

/**
Created by kelvin_clark on 12/5/2020
 */
class NotesRecyclerAdapter (val listener: ClickListener)
    : ListAdapter<Note, NotesRecyclerAdapter.NotesViewHolder>(DiffUtilCallback()) {

    private var _binding : NoteListLayoutBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        _binding = NoteListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesViewHolder(_binding!!)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.populateViews(getItem(position))
    }

    fun clearUp() {
        _binding = null
    }

    inner class NotesViewHolder(private val binding: NoteListLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val note = getItem(position)
                        listener.onClickListener(note, position)
                    }
                }
            }
        }

        fun populateViews(note :Note) {
            binding.apply {
                textTitle.text = note.title
                textBody.text = note.body
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
    }
}