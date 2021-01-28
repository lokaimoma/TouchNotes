package com.koc.touchnotes.view

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.koc.touchnotes.R
import com.koc.touchnotes.databinding.FragmentNoteListBinding
import com.koc.touchnotes.enums.NoteSort
import com.koc.touchnotes.interfaces.ClickListener
import com.koc.touchnotes.model.Note
import com.koc.touchnotes.util.NoteEvent
import com.koc.touchnotes.util.exhaustive
import com.koc.touchnotes.view.extensions.queryTextListener
import com.koc.touchnotes.viewModel.NoteListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

/**
 * Created by kelvin_clark on 5/12/20
 */
@AndroidEntryPoint
class NoteListFragment : Fragment(), ClickListener {
    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    var notesAdapter = NotesRecyclerAdapter(this)

    private val noteListViewModel: NoteListViewModel by viewModels()

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

        binding.apply {
            itemsNotes.adapter = notesAdapter
            itemsNotes.layoutManager = GridLayoutManager(context, 2,
                GridLayoutManager.VERTICAL, false)
            fabAdd.setOnClickListener {
                noteListViewModel.addNoteClicked()
            }
        }

        observeNoteList()
        collectFlows()
    }

    private fun collectFlows() = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
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
    private fun observeNoteList() {
        noteListViewModel.getAllNotes().observe(viewLifecycleOwner) { notesList ->
            notesAdapter.submitList(notesList)
        }
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
            else -> return super.onOptionsItemSelected(item)
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