package com.koc.touchnotes.view.extensions

import android.graphics.Color
import android.view.animation.AnticipateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.koc.touchnotes.R
import com.koc.touchnotes.util.NoteEvent
import com.koc.touchnotes.util.exhaustive
import com.koc.touchnotes.view.MainActivity
import com.koc.touchnotes.view.NoteListFragment
import com.koc.touchnotes.view.NoteListFragmentDirections
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
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
            return true
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
                itemsNotes = null
                ivEmpty = null
                tvEmpty = null
            }
            is NoteEvent.AddNoteEvent -> {
                findNavController().navigate(R.id.action_list_edit)
            }
            is NoteEvent.NoteSwipedEvent -> {
                Snackbar.make(requireView(), "Note deleted", Snackbar.LENGTH_SHORT)
                    .setAction("Undo") {
                        noteListViewModel.restoreNote(event.note)
                    }
                    .setBackgroundTint(Color.BLACK)
                    .setTextColor(Color.WHITE)
                    .show()
            }
            NoteEvent.GotoSettingsScreen -> {
                findNavController().navigate(R.id.action_list_settings)
            }
        }.exhaustive
    }
}

@ExperimentalCoroutinesApi
fun NoteListFragment.observeNoteList() {
    noteListViewModel.getAllNotes().observe(viewLifecycleOwner) { notesList ->
        notesAdapter.submitList(notesList) {

            itemsNotes = itemsNotes ?: binding.itemsNotesStub.inflate() as RecyclerView?

            if ((requireActivity() as MainActivity).isNoteSavedOrUpdated) {
                setUpViews(0)
            } else {
                setUpViews(noteListViewModel.lastRecyclerViewPosition)
            }

            if (notesList.isEmpty()) {
                ivEmpty = ivEmpty ?: binding.ivEmptyStub.inflate() as ImageView?
                tvEmpty = tvEmpty ?: binding.tvEmptyStub.inflate() as TextView?
                ivEmpty?.isVisible = true
                tvEmpty?.isVisible = true
            } else {
                ivEmpty?.isVisible = false
                tvEmpty?.isVisible = false
            }
        }
    }
}

fun NoteListFragment.setUpViews(recyclerViewPosition: Int) {

    binding.apply {
        itemsNotes?.adapter = notesAdapter
        itemsNotes?.itemAnimator = SlideInLeftAnimator(AnticipateInterpolator(1f))

        itemsNotes?.layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
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
                (requireActivity() as MainActivity).isNoteSavedOrUpdated = true
                noteListViewModel.noteSwiped(note)
            }

        }).attachToRecyclerView(itemsNotes)

        itemsNotes?.scrollToPosition(recyclerViewPosition)

        fabAdd.setOnClickListener {
            noteListViewModel.addNoteClicked()
        }

    }
}