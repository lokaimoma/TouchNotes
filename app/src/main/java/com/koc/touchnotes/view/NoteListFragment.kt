package com.koc.touchnotes.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.koc.touchnotes.databinding.FragmentNoteListBinding
import com.koc.touchnotes.model.Note
import com.koc.touchnotes.viewModel.NoteListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by kelvin_clark on 5/12/20
 */
@AndroidEntryPoint
class NoteListFragment : Fragment() {
    var _binding: FragmentNoteListBinding? = null
    val binding get() = _binding!!

    @Inject
    lateinit var notesAdapter: NotesRecyclerAdapter

    @Inject
    lateinit var noteListViewModel: NoteListViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.itemsNotes.apply {
            adapter = notesAdapter
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        }
        observeNoteList()
    }

    private fun observeNoteList() {
        noteListViewModel.getAllNotes().observe(viewLifecycleOwner, { notesList ->
            notesAdapter.updateList(notesList)

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        notesAdapter.clearUp()
    }
}