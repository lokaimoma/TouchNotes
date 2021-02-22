package com.koc.touchnotes.view

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.koc.touchnotes.R
import com.koc.touchnotes.databinding.FragmentNoteListBinding
import com.koc.touchnotes.enums.NoteLayout
import com.koc.touchnotes.enums.NoteSort
import com.koc.touchnotes.interfaces.ClickListener
import com.koc.touchnotes.model.Note
import com.koc.touchnotes.view.extensions.*
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
        noteListViewModel.collectNoteLayoutStyle()
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

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val layoutToggle = menu.findItem(R.id.actionChangeLayout)
        if (noteListViewModel.layoutStyle == NoteLayout.GRID_VIEW) {
            layoutToggle.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_list, null)
            layoutToggle.title = resources.getString(R.string.list_style)
        } else {
            layoutToggle.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_grid, null)
            layoutToggle.title = resources.getString(R.string.grid_style)
        }
        setUpViews()
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
            R.id.actionChangeLayout -> {
                changeLayout(item)
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