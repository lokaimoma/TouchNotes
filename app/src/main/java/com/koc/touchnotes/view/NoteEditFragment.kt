package com.koc.touchnotes.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.koc.touchnotes.R
import com.koc.touchnotes.databinding.FragmentNoteEditBinding
import com.koc.touchnotes.viewModel.NoteEditViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteEditFragment : Fragment() {
    private var _binding : FragmentNoteEditBinding? = null
    private val binding get() = _binding!!

    val noteEditViewModel : NoteEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentNoteEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.note_edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.saveNote){
            noteEditViewModel.saveNote(binding.noteTitle.text.toString(), binding.noteBody.text.toString())
            return true
        }else {
            return super.onOptionsItemSelected(item)
        }
    }
}