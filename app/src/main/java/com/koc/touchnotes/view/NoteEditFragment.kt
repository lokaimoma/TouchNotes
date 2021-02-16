package com.koc.touchnotes.view

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.koc.touchnotes.R
import com.koc.touchnotes.databinding.FragmentNoteEditBinding
import com.koc.touchnotes.util.NoteEditEvent
import com.koc.touchnotes.util.exhaustive
import com.koc.touchnotes.view.extensions.*
import com.koc.touchnotes.viewModel.NoteEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class NoteEditFragment : Fragment() {
    private var _binding: FragmentNoteEditBinding? = null
    val binding get() = _binding!!

    var noteId: Int? = null
    var createdTime: Long? = null
    var modifiedTime: Long? = null
    var isModified = false

    val noteEditViewModel: NoteEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNoteEditBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateViews()
        saveNoteState()
        binding.noteTitle.doAfterTextChanged {
            isModified = true
        }
        binding.noteBody.doAfterTextChanged {
            isModified = true
        }
        collectFlows()
    }

    private fun collectFlows(): Job = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        noteEditViewModel.noteEditEvent.first { event ->
            return@first when (event) {
                is NoteEditEvent.NoteSavedEvent -> {
                    noteId = event.id
                    true
                }
            }.exhaustive
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.note_edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionSave -> {
                saveNote()
                if (noteId != null) {
                    Snackbar.make(binding.root, "Note updated", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.BLACK)
                        .setTextColor(Color.WHITE)
                        .show()
                } else {
                    Snackbar.make(binding.root, "Note saved", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.BLACK)
                        .setTextColor(Color.WHITE)
                        .show()
                }
                true
            }
            R.id.actionDelete -> {
                showDeleteDialogue()
                true
            }
            R.id.actionShare -> {
                shareNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val deleteButton = menu.findItem(R.id.actionDelete)
        deleteButton.isVisible = noteId != null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}