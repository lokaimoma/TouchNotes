package com.koc.touchnotes.viewModel

import android.text.SpannableStringBuilder
import android.text.Spanned
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koc.touchnotes.model.NoteRepository
import com.koc.touchnotes.model.entities.Note
import com.koc.touchnotes.model.entities.TextSpan
import com.koc.touchnotes.util.Constants.IS_BOLD
import com.koc.touchnotes.util.Constants.IS_ITALIC
import com.koc.touchnotes.util.Constants.IS_STRIKE_THROUGH
import com.koc.touchnotes.util.Constants.IS_UNDERLINED
import com.koc.touchnotes.util.NoteEditEvent
import com.koc.touchnotes.viewModel.ext.createNote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
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

    fun applySpan(noteId: Int, spanName: String, span: Any, textStart: Int, textEnd: Int) =
        viewModelScope.launch {
            if ((textStart != -1) && (textEnd != -1)) {
                body.setSpan(span, textStart, textEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                _noteEditChannel.send(NoteEditEvent.TextSpannedEvent(textStart, textEnd))
                saveSpan(noteId, spanName, textStart, textEnd)
            }
        }

    private suspend fun saveSpan(noteId: Int, spanName: String, textStart: Int, textEnd: Int)= withContext(IO) {
        when (spanName) {
            IS_BOLD -> repository.insertTextSpan(TextSpan(noteId = noteId,
                isBold = true, textStart = textStart, textEnd = textEnd))

            IS_ITALIC -> repository.insertTextSpan(TextSpan(noteId = noteId,
                isItalic = true, textStart = textStart, textEnd = textEnd))

            IS_UNDERLINED -> repository.insertTextSpan(TextSpan(noteId = noteId,
                isUnderlined = true, textStart = textStart, textEnd = textEnd))

            IS_STRIKE_THROUGH -> repository.insertTextSpan(TextSpan(noteId = noteId,
                isStrikeThrough = true, textStart = textStart, textEnd = textEnd))
        }
    }

    companion object {
        private const val NOTE_TITLE = "noteTitle"
        private const val NOTE_BODY = "noteBody"
        private const val NOTE = "note"
    }
}