package com.koc.touchnotes.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.koc.touchnotes.databinding.NoteListLayoutBinding
import com.koc.touchnotes.model.Note

/**
Created by kelvin_clark on 12/5/2020
 */
class NotesRecyclerAdapter : RecyclerView.Adapter<NotesRecyclerAdapter.NotesViewHolder>() {
    var _binding : NoteListLayoutBinding? = null
    var notes = ArrayList<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        _binding = NoteListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesViewHolder(_binding!!)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.populateViews(notes[position])
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun clearUp() {
        _binding = null
    }

    class NotesViewHolder(binding: NoteListLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val textTitle = binding.textTitle
        val textBody = binding.textBody

        fun populateViews(note :Note) {
            textBody.text = note.body
            textTitle.text = note.title
        }
    }
}