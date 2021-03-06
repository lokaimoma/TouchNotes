package com.koc.touchnotes.view.extensions

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.text.SpannableStringBuilder
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.koc.touchnotes.R
import com.koc.touchnotes.view.MainActivity
import com.koc.touchnotes.view.NoteEditFragment
import kotlinx.coroutines.launch
import java.util.*

/**
Created by kelvin_clark on 1/29/2021 3:04 AM
 */

fun NoteEditFragment.saveNoteState() {
    binding.apply {
        noteTitle.addTextChangedListener {
            if (noteEditViewModel.note?.title != it.toString()) {
                noteEditViewModel.title = it.toString()
                isModified = true
            }

        }

        noteBody.addTextChangedListener {
            if (noteEditViewModel.note?.body.toString() != it.toString()) {
                noteEditViewModel.body = SpannableStringBuilder(it)
                isModified = true
            }
        }
    }
}

fun NoteEditFragment.populateViews() = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
    binding.apply {
        noteTitle.setText(noteEditViewModel.title)
        noteBody.text = noteEditViewModel.body
        noteId = noteEditViewModel.note?.id
        createdTime = noteEditViewModel.note?._createdTime
        modifiedTime = noteEditViewModel.note?._modifiedTime

        if (noteTitle.text?.isNotEmpty() == true) {
            val dateCreated = Date(createdTime ?: System.currentTimeMillis())
            val dateModified = Date(modifiedTime ?: System.currentTimeMillis())
            val dayTimeFormatter = SimpleDateFormat("EEEE, dd-MM-yyyy hh:mm", Locale.getDefault())
            timeCreated.text = dayTimeFormatter.format(dateCreated)
            timeModified.text = dayTimeFormatter.format(dateModified)
        } else {
            timeCreated.isVisible = false
            timeModified.isVisible = false
            createdTimeTitle.isVisible = false
            modifiedTimeTitle.isVisible = false
        }
    }
}

fun NoteEditFragment.saveNote(forceSave: Boolean = false, onComplete: (() -> Unit)? = null) {
    val time = System.currentTimeMillis()
    lifecycleScope.launch {
        if (noteId != null) {
            if (isModified) {
                (requireActivity() as MainActivity).isNoteSavedOrUpdated = true
                noteEditViewModel.updateNote(
                    noteId!!, binding.noteTitle.text.toString(),
                    noteBody = binding.noteBody.text.toString(),
                    createdTime = createdTime!!, time, onComplete
                )
            } else {
                (requireActivity() as MainActivity).isNoteSavedOrUpdated = false
            }
        } else {
            if (binding.noteTitle.text.toString() != "" || binding.noteBody.text.toString() != "" || forceSave) {
                (requireActivity() as MainActivity).isNoteSavedOrUpdated = true
                createdTime = time
                noteEditViewModel.saveNote(
                    binding.noteTitle.text.toString(),
                    binding.noteBody.text.toString(), time, time,
                    onComplete
                )
            } else {
                (requireActivity() as MainActivity).isNoteSavedOrUpdated = false
            }
        }
    }
}

fun NoteEditFragment.showShareMethodDialog() {
    val choices = arrayOf(getString(R.string.pdf), getString(R.string.plain_text))
    val shareMethodDialog = MaterialAlertDialogBuilder(requireContext())

    shareMethodDialog.apply {
        setTitle(getString(R.string.share_as))
        setItems(choices) {dialog, which ->
            when(which) {
                0 -> createEmptyPDFFile()
                1 -> shareAsMessage()
                else -> dialog.dismiss()
            }
        }
    }
    shareMethodDialog.show()
}

fun NoteEditFragment.shareAsPDF() {
    sharePDFResultLauncher.launch(binding.noteTitle.text.toString())
}

fun NoteEditFragment.shareAsMessage() {
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


fun NoteEditFragment.showDeleteDialogue() {
    val deleteAlert = MaterialAlertDialogBuilder(requireContext())

    deleteAlert.apply {
        setTitle(getString(R.string.delete_note))
        setMessage(getString(R.string.delete_confirmation))
        setIcon(R.drawable.ic_delete_dialogue)
        setPositiveButton(getString(R.string.yes)) { dialogue, _ ->
            noteEditViewModel.deleteNote(noteId)
            dialogue.dismiss()
            Toast.makeText(
                requireContext(),
                getString(R.string.note_delete_success),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
        }
        setNegativeButton(getString(R.string.no)) { dialogue, _ ->
            dialogue.dismiss()
        }
        setCancelable(false)
    }

    deleteAlert.create().show()
}