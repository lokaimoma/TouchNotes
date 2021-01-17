package com.koc.touchnotes.view

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.koc.touchnotes.R
import com.koc.touchnotes.databinding.FragmentNoteEditBinding
import com.koc.touchnotes.model.Note
import com.koc.touchnotes.viewModel.NoteEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NoteEditFragment : Fragment() {
    private var _binding : FragmentNoteEditBinding? = null
    private val binding get() = _binding!!

    private val args: NoteEditFragmentArgs by navArgs()
    private var noteId: Int? = null

    @Inject
    lateinit var noteEditViewModel : NoteEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentNoteEditBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val note : Note? = args.note

        if(note != null){
            binding.noteTitle.setText(note.title)
            binding.noteBody.setText(note.body)
            noteId = note.id
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.note_edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.actionSave -> {
                if (noteId != null) {
                    noteEditViewModel.updateNote(noteId!!, binding.noteTitle.text.toString(), binding.noteBody.text.toString())
                    Snackbar.make(binding.root, "Note updated", Snackbar.LENGTH_SHORT).show()
                }else {
                    noteEditViewModel.saveNote(binding.noteTitle.text.toString(), binding.noteBody.text.toString())
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