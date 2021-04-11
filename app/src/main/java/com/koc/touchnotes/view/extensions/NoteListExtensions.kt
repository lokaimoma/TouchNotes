package com.koc.touchnotes.view.extensions

import android.graphics.Color
import android.util.Log
import android.view.MenuItem
import android.view.animation.AnticipateInterpolator
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import com.koc.touchnotes.R
import com.koc.touchnotes.enums.NoteLayout
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
            is NoteEvent.UpdateNoteLayoutStyleEvent -> {
                if (event.layoutStyle == NoteLayout.GRID_VIEW) {
                    itemsNotes?.layoutManager = GridLayoutManager(
                        context, 2,
                        GridLayoutManager.VERTICAL, false
                    )
                } else {
                    itemsNotes?.layoutManager = LinearLayoutManager(context)
                }
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

            itemsNotes = binding.itemsNotesStub.inflate() as RecyclerView?
            setUpViews()

//            if ((requireActivity() as MainActivity).isNoteSavedOrUpdated){
//                itemsNotes!!.smoothScrollToPosition(0)
//            }else {
//                itemsNotes!!.smoothScrollToPosition(noteListViewModel.lastRecyclerViewPosition)
//            }
        }
        if (notesList.isEmpty()) {
            binding.ivEmpty.isVisible = true
            binding.tvEmpty.isVisible = true
        } else {
            binding.ivEmpty.isVisible = false
            binding.tvEmpty.isVisible = false
        }
    }
}

fun NoteListFragment.setUpViews() {

    binding.apply {
        itemsNotes?.adapter = notesAdapter
        itemsNotes?.itemAnimator = SlideInLeftAnimator(AnticipateInterpolator(1f))

        itemsNotes?.layoutManager = if (noteListViewModel.layoutStyle == NoteLayout.LINEAR_VIEW)
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        else
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

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
        fabAdd.setOnClickListener {
            noteListViewModel.addNoteClicked()
        }

    }
}

fun NoteListFragment.changeLayout(item: MenuItem) {
    if (item.title.toString() == resources.getString(R.string.list_style)) {
        item.title = resources.getString(R.string.grid_style)
        item.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_grid, null)
        noteListViewModel.updateNoteLayoutStyle(NoteLayout.LINEAR_VIEW)
    } else {
        item.title = resources.getString(R.string.list_style)
        item.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_list, null)
        noteListViewModel.updateNoteLayoutStyle(NoteLayout.GRID_VIEW)
    }
}