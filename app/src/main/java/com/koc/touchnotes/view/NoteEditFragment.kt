package com.koc.touchnotes.view

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.koc.touchnotes.R
import com.koc.touchnotes.databinding.FragmentNoteEditBinding
import com.koc.touchnotes.viewModel.NoteEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteEditFragment : Fragment() {
    private var _binding: FragmentNoteEditBinding? = null
    private val binding get() = _binding!!

    private var noteId: Int? = null
    private var createdTime: Long? = null
    private var modifiedTime: Long? = null
    private var isModified = false

    private val noteEditViewModel: NoteEditViewModel by viewModels()

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
    }

    private fun saveNoteState() {
        binding.apply {
            noteTitle.addTextChangedListener {
                noteEditViewModel.title = it.toString()
            }

            noteBody.addTextChangedListener {
                noteEditViewModel.body = it.toString()
            }
        }
    }

    private fun populateViews() = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        binding.apply {
            noteTitle.setText(noteEditViewModel.title)
            noteBody.setText(noteEditViewModel.body)
            noteId = noteEditViewModel.note?.id
            createdTime = noteEditViewModel.note?._createdTime
            modifiedTime = noteEditViewModel.note?._modifiedTime
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
                    Snackbar.make(binding.root, "Note updated", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(binding.root, "Note saved", Snackbar.LENGTH_SHORT).show()
                }
                true
            }

            R.id.actionDelete -> {
                noteEditViewModel.deleteNote(noteId)
                this.findNavController().navigate(R.id.action_edit_list)
                true
            }

            R.id.actionShare -> {
                shareNote()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveNote() {
        val time = System.currentTimeMillis()
        lifecycleScope.launch(IO) {
            if (noteId != null) {
                if (isModified) {
                    noteEditViewModel.updateNote(
                        noteId!!, binding.noteTitle.text.toString(),
                        noteBody = binding.noteBody.text.toString(),
                        createdTime = createdTime!!, time
                    )
                }
            } else {
                noteEditViewModel.saveNote(
                    binding.noteTitle.text.toString(),
                    binding.noteBody.text.toString(), time, time
                )
            }
        }
    }

    private fun shareNote() {
        val title = binding.noteTitle.text.toString()
        val body = binding.noteBody.text.toString()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "Share note")
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        startActivity(Intent.createChooser(sendIntent, resources.getString(R.string.share)))
        Toast.makeText(context, "Sharing", Toast.LENGTH_SHORT).show()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val deleteButton = menu.findItem(R.id.actionDelete)
        deleteButton.isVisible = noteId != null
    }

}