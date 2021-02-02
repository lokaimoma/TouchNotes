package com.koc.touchnotes.view.extensions

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.koc.touchnotes.R
import com.koc.touchnotes.enums.NoteLayout
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
    crossinline func: (String) -> Unit
) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

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
    noteListViewModel.noteEvent.collect { event ->
        when (event) {
            is NoteEvent.NoteClickedEvent -> {
                val action = NoteListFragmentDirections.actionListEdit(event.note)
                findNavController().navigate(action)
            }
            is NoteEvent.AddNoteEvent -> {
                findNavController().navigate(R.id.action_list_edit)
            }
            is NoteEvent.NoteSwipedEvent -> {
                Snackbar.make(requireView(), "Note deleted", Snackbar.LENGTH_SHORT)
                    .setAction("Undo") {
                        noteListViewModel.restoreNote(event.note)
                    }.show()
            }
            is NoteEvent.UpdateNoteLayoutStyleEvent -> {
                if (event.layoutStyle == NoteLayout.GRID_VIEW) {
                    binding.itemsNotes.layoutManager = GridLayoutManager(
                        context, 2,
                        GridLayoutManager.VERTICAL, false
                    )
                } else {
                    binding.itemsNotes.layoutManager = LinearLayoutManager(context)
                }
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

        itemsNotes.layoutManager = if (noteListViewModel.layoutStyle == NoteLayout.LINEAR_VIEW)
            LinearLayoutManager(context)
        else
            GridLayoutManager(
                context,
                2,
                GridLayoutManager.VERTICAL,
                false
            )

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val note = notesAdapter.currentList[viewHolder.adapterPosition]
                noteListViewModel.noteSwiped(note)
            }

        }).attachToRecyclerView(itemsNotes)

        fabAdd.setOnClickListener {
            noteListViewModel.addNoteClicked()
        }
    }
}