package com.koc.touchnotes.view

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.*
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
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
        _binding = FragmentNoteEditBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateViews()
        saveNoteState()
        collectFlows()

        viewLifecycleOwner.lifecycleScope.launch(IO) {
            binding.noteBody.customSelectionActionModeCallback = object : ActionMode.Callback {
                override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    mode?.menuInflater?.inflate(R.menu.text_span_menu, menu)
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = false

                override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                    when(item?.itemId){
                        R.id.actionBold -> {
                            noteEditViewModel.applySpan(
                                StyleSpan(Typeface.BOLD),
                                binding.noteBody.selectionStart,
                                binding.noteBody.selectionEnd
                            )
                            mode?.finish()
                            return true
                        }
                        R.id.actionItalic -> {
                            noteEditViewModel.applySpan(
                                StyleSpan(Typeface.ITALIC),
                                binding.noteBody.selectionStart,
                                binding.noteBody.selectionEnd
                            )
                            mode?.finish()
                            return true
                        }
                        R.id.actionUnderline -> {
                            noteEditViewModel.applySpan(
                                UnderlineSpan(),
                                binding.noteBody.selectionStart,
                                binding.noteBody.selectionEnd
                            )
                            mode?.finish()
                            return true
                        }
                        R.id.actionStrikeThrough -> {
                            noteEditViewModel.applySpan(
                                StrikethroughSpan(),
                                binding.noteBody.selectionStart,
                                binding.noteBody.selectionEnd
                            )
                            mode?.finish()
                            return true
                        }
                        else -> return false
                    }
                }

                override fun onDestroyActionMode(mode: ActionMode?) {
                    mode?.hide(10)
                }
            }
        }
    }

    override fun onPause() {
        saveNote()
        super.onPause()
    }

    private fun collectFlows(): Job = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        noteEditViewModel.noteEditEvent.first { event ->
            return@first when (event) {
                is NoteEditEvent.NoteSavedEvent -> {
                    noteId = event.id
                    requireActivity().invalidateOptionsMenu()
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
                saveNote(true)
                if (noteId != null) {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.note_updated_msg),
                        Snackbar.LENGTH_SHORT
                    )
                        .setBackgroundTint(Color.BLACK)
                        .setTextColor(Color.WHITE)
                        .show()
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.note_saved_msg),
                        Snackbar.LENGTH_SHORT
                    )
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