package com.koc.touchnotes.view

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.koc.touchnotes.R
import com.koc.touchnotes.databinding.FragmentNoteListBinding
import com.koc.touchnotes.enums.NoteSort
import com.koc.touchnotes.view.extensions.queryTextListener
import com.koc.touchnotes.viewModel.NoteListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

/**
 * Created by kelvin_clark on 5/12/20
 */
@AndroidEntryPoint
class NoteListFragment : Fragment() {
    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var notesAdapter: NotesRecyclerAdapter

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
                val action = NoteListFragmentDirections.actionListEdit()
                Navigation.findNavController(it).navigate(action)
            }
        }

        observeNoteList()
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
}