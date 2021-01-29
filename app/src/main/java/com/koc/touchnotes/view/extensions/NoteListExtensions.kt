package com.koc.touchnotes.view.extensions

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.koc.touchnotes.R
import com.koc.touchnotes.util.NoteEvent
import com.koc.touchnotes.util.exhaustive
import com.koc.touchnotes.view.NoteListFragment
import com.koc.touchnotes.view.NoteListFragmentDirections
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

/**
Created by kelvin_clark on 1/24/2021 6:33 PM
 */

inline fun SearchView.queryTextListener(
    crossinline func: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener{

        override fun onQueryTextSubmit(query: String?): Boolean {
            return true //
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            func(newText.orEmpty())
            return true
        }
    })
}

fun NoteListFragment.collectFlows() = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
    noteListViewModel.noteEvent.collect {event ->
        when(event){
            is NoteEvent.NoteClickedEvent -> {
                val action = NoteListFragmentDirections.actionListEdit(event.note)
                findNavController().navigate(action)
            }
            is NoteEvent.AddNoteEvent -> {
                findNavController().navigate(R.id.action_list_edit)
            }
        }.exhaustive
    }
}

@ExperimentalCoroutinesApi
fun NoteListFragment.observeNoteList() {
    noteListViewModel.getAllNotes().observe(viewLifecycleOwner) { notesList ->
        notesAdapter.submitList(notesList)
    }
}

fun NoteListFragment.setUpViews() {
    binding.apply {
        itemsNotes.adapter = notesAdapter
        itemsNotes.layoutManager = GridLayoutManager(
            context, 2,
            GridLayoutManager.VERTICAL, false
        )
        fabAdd.setOnClickListener {
            noteListViewModel.addNoteClicked()
        }
    }
}