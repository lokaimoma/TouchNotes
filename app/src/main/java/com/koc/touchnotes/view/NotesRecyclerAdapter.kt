package com.koc.touchnotes.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.koc.touchnotes.databinding.NoteListLayoutBinding
import com.koc.touchnotes.model.Note
import javax.inject.Inject

/**
Created by kelvin_clark on 12/5/2020
 */
class NotesRecyclerAdapter @Inject constructor()
    : RecyclerView.Adapter<NotesRecyclerAdapter.NotesViewHolder>() {
    private var _binding : NoteListLayoutBinding? = null

    private var notesDiffUtilCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean =
            oldItem.id == newItem.id
    }

    private var differ = AsyncListDiffer(this, notesDiffUtilCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        _binding = NoteListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesViewHolder(_binding!!)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.populateViews(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun clearUp() {
        _binding = null
    }

    fun updateList(newNote: List<Note>) {
        differ.submitList(newNote)
    }

    inner class NotesViewHolder(private val binding: NoteListLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun populateViews(note :Note) {
            binding.apply {
                textTitle.text = note.title
                textBody.text = note.body

            }
            binding.root.setOnClickListener {
                val action = NoteListFragmentDirections.actionListEdit(note)
                it.findNavController().navigate(action)
            }
        }
    }
}