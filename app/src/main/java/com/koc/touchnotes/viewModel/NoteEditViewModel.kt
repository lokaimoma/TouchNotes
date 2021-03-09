package com.koc.touchnotes.viewModel

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koc.touchnotes.model.entities.Note
import com.koc.touchnotes.model.NoteRepository
import com.koc.touchnotes.model.entities.TextSpan
import com.koc.touchnotes.util.NoteEditEvent
import com.koc.touchnotes.viewModel.ext.createNote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
Created by kelvin_clark on 12/20/2020
 */
@HiltViewModel
class NoteEditViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val noteState: SavedStateHandle
) : ViewModel() {

    private val _noteEditChannel = Channel<NoteEditEvent>()
    val noteEditEvent = _noteEditChannel.receiveAsFlow()

    val note = noteState.get<Note>(NOTE)

    var title = noteState.get<String>(NOTE_TITLE) ?: note?.title ?: ""
        set(value) {
            field = value
            noteState.set(NOTE_TITLE, value)
        }

    var body = SpannableStringBuilder(noteState.get<String>(NOTE_BODY) ?: note?.body ?: "")
        set(value) {
            field = value
            noteState.set(NOTE_BODY, value.toString())
        }

    fun saveNote(
        noteTitle: String, noteBody: String, createdTime: Long, modifiedTime: Long,
        onComplete: (() -> Unit)?
    ) {
        viewModelScope.launch {
            val note = createNote(noteTitle, noteBody, createdTime, modifiedTime)
            val noteId: Long?
            noteId = withContext(IO) { repository.insertNote(note) }
            onComplete?.invoke()
                ?: _noteEditChannel.send(NoteEditEvent.NoteSavedEvent(noteId.toInt()))
        }
    }

    fun updateNote(
        noteId: Int,
        noteTitle: String,
        noteBody: String,
        createdTime: Long,
        modifiedTime: Long,
        onComplete: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            if (noteTitle != "" && noteBody != "") {
                withContext(IO) {
                    repository.updateNote(
                        Note(
                            noteTitle, noteBody, id = noteId, _createdTime = createdTime,
                            _modifiedTime = modifiedTime
                        )
                    )
                }
            }
            onComplete?.invoke()
        }
    }

    fun deleteNote(noteId: Int?) {
        viewModelScope.launch(IO) {
            repository.removeNote(noteId!!)
        }
    }

    fun getSpans() = viewModelScope.launch {
        note?.id?.let { repository.getSpans(it) }?.first { textSPans ->
            for (span in textSPans) {
                if (span.isBold) {
                    applySpan(span.textStart, span.textEnd, StyleSpan(Typeface.BOLD))
                } else if (span.isItalic) {
                    applySpan(span.textStart, span.textEnd, StyleSpan(Typeface.ITALIC))
                } else if (span.isUnderlined) {
                    applySpan(span.textStart, span.textEnd, UnderlineSpan())
                } else if (span.isStrikeThrough) {
                    applySpan(span.textStart, span.textEnd, StrikethroughSpan())
                }
            }
            true
        }
    }

    fun applySpan(start: Int, end: Int, style: Any) {
        if ((start != -1) && (end != -1)) {
            body.setSpan(style, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    fun saveSpan(textSpan: TextSpan) = viewModelScope.launch(IO) {
        repository.insertTextSpan(textSpan)
    }

    companion object {
        private const val NOTE_TITLE = "noteTitle"
        private const val NOTE_BODY = "noteBody"
        private const val NOTE = "note"
    }
}