package com.koc.touchnotes.view

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.koc.touchnotes.R
import com.koc.touchnotes.databinding.FragmentNoteListBinding
import com.koc.touchnotes.enums.NoteSort
import com.koc.touchnotes.interfaces.ClickListener
import com.koc.touchnotes.model.entities.Note
import com.koc.touchnotes.view.extensions.collectFlows
import com.koc.touchnotes.view.extensions.observeNoteList
import com.koc.touchnotes.view.extensions.queryTextListener
import com.koc.touchnotes.viewModel.NoteListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Created by kelvin_clark on 5/12/20
 */
@AndroidEntryPoint
class NoteListFragment : Fragment(), ClickListener {
    private var _binding: FragmentNoteListBinding? = null
    val binding get() = _binding!!

    var notesAdapter = NotesRecyclerAdapter(this)

    val noteListViewModel: NoteListViewModel by viewModels()

    var itemsNotes: RecyclerView? = null
    var ivEmpty : ImageView? = null
    var tvEmpty: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        observeNoteList()
        collectFlows()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.note_list_menu, menu)

        val searchItem = menu.findItem(R.id.actionSearch)
        val searchView = searchItem.actionView as SearchView

        searchView.queryTextListener {
            noteListViewModel.searchQuery.value = it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionSortByCreatedTime -> {
                noteListViewModel.updateSortOrder(NoteSort.BY_CREATED_TIME)
                true
            }

            R.id.actionSortByModifiedTime -> {
                noteListViewModel.updateSortOrder(NoteSort.BY_MODIFIED_TIME)
                true
            }
            R.id.actionSortByTitle -> {
                noteListViewModel.updateSortOrder(NoteSort.BY_TITLE)
                true
            }
            R.id.actionSettings -> {
                noteListViewModel.requestSettingsScreen()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        notesAdapter.clearUp()
    }

    override fun onClickListener(note: Note, position: Int) {
        noteListViewModel.noteClicked(note)
        noteListViewModel.lastRecyclerViewPosition = position
    }
}