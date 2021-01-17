package com.koc.touchnotes.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.koc.touchnotes.databinding.NoteListLayoutBinding
import com.koc.touchnotes.model.Note
import com.koc.touchnotes.utils.NotesDifUtil
import javax.inject.Inject

/**
Created by kelvin_clark on 12/5/2020
 */
class NotesRecyclerAdapter @Inject constructor() : RecyclerView.Adapter<NotesRecyclerAdapter.NotesViewHolder>() {
    private var _binding : NoteListLayoutBinding? = null

    var notesDiffutilCallback : NotesDifUtil = NotesDifUtil()
    private var differ = AsyncListDiffer(this, notesDiffutilCallback)

    init {
        setHasStableIds(true)
    }

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

    override fun getItemId(position: Int): Long {
        val id = differ.currentList[position].id
        return id.toLong()
    }

    class NotesViewHolder(binding: NoteListLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        private val textTitle = binding.textTitle
        private val textBody = binding.textBody
        val view = binding.root

        fun populateViews(note :Note) {
            textBody.text = note.body
            textTitle.text = note.title
            view.setOnClickListener {
                val action = NoteListFragmentDirections.actionListEdit(note)
                it.findNavController().navigate(action)
            }
        }
    }
}