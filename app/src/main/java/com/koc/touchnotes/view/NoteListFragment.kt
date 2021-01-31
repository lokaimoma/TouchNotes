package com.koc.touchnotes.view

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.koc.touchnotes.R
import com.koc.touchnotes.databinding.FragmentNoteListBinding
import com.koc.touchnotes.enums.NoteLayout
import com.koc.touchnotes.enums.NoteSort
import com.koc.touchnotes.interfaces.ClickListener
import com.koc.touchnotes.model.Note
import com.koc.touchnotes.view.extensions.collectFlows
import com.koc.touchnotes.view.extensions.observeNoteList
import com.koc.touchnotes.view.extensions.queryTextListener
import com.koc.touchnotes.view.extensions.setUpViews
import com.koc.touchnotes.viewModel.NoteListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first

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
        return _binding?.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setUpViews()
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
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            val noteLayout = noteListViewModel.noteLayoutStyle.first()
            if (noteLayout == NoteLayout.GRID_VIEW){
                layoutToggle.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_list, null)
                layoutToggle.title = resources.getString(R.string.list_style)
            }else {
                layoutToggle.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_grid, null)
                layoutToggle.title = resources.getString(R.string.grid_style)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
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
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun changeLayout(item: MenuItem) {
        if (item.title.toString() == resources.getString(R.string.list_style)) {
            item.title = resources.getString(R.string.grid_style)
            item.icon =  ResourcesCompat.getDrawable(resources, R.drawable.ic_grid, null)
            noteListViewModel.updateNoteLayoutStyle(NoteLayout.LINEAR_VIEW)
        }else {
            item.title = resources.getString(R.string.list_style)
            item.icon =  ResourcesCompat.getDrawable(resources, R.drawable.ic_list, null)
            noteListViewModel.updateNoteLayoutStyle(NoteLayout.GRID_VIEW)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        notesAdapter.clearUp()
    }

    override fun onClickListener(note: Note) {
        noteListViewModel.noteClicked(note)
    }
}