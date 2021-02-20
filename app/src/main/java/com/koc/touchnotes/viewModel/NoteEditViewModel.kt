package com.koc.touchnotes.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koc.touchnotes.model.Note
import com.koc.touchnotes.model.NoteDatabase
import com.koc.touchnotes.model.Repository
import com.koc.touchnotes.util.NoteEditEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
Created by kelvin_clark on 12/20/2020
 */
@HiltViewModel
class NoteEditViewModel @Inject constructor(
    private val repository: Repository,
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

    var body = noteState.get<String>(NOTE_BODY) ?: note?.body ?: ""
        set(value) {
            field = value
            noteState.set(NOTE_BODY, value)
        }

    fun saveNote(noteTitle: String, noteBody: String, createdTime: Long, modifiedTime: Long) {
        viewModelScope.launch(IO) {
            val noteId: Long?
            if ((noteTitle != "") && (noteBody != "")) {
                noteId = repository
                    .insertNote(Note(noteTitle, noteBody, createdTime, modifiedTime))
            } else if (noteTitle == "") {
                if (noteBody != "") {
                    noteId = repository.insertNote(
                        Note(
                            body = noteBody, _createdTime = createdTime,
                            _modifiedTime = modifiedTime
                        )
                    )
                } else {
                    noteId = repository.insertNote(
                        Note(
                            _createdTime = createdTime,
                            _modifiedTime = modifiedTime
                        )
                    )
                }
            } else if (noteBody == "") {
                noteId = if (noteTitle != "") {
                    repository.insertNote(
                        Note(
                            title = noteTitle, _createdTime = createdTime,
                            _modifiedTime = modifiedTime
                        )
                    )
                } else {
                    repository.insertNote(
                        Note(
                            _createdTime = createdTime,
                            _modifiedTime = modifiedTime
                        )
                    )
                }
            } else {
                noteId = repository.insertNote(
                    Note(
                        _createdTime = createdTime,
                        _modifiedTime = modifiedTime
                    )
                )
            }
            _noteEditChannel.send(NoteEditEvent.NoteSavedEvent(noteId.toInt()))
        }
    }

    fun updateNote(
        noteId: Int,
        noteTitle: String,
        noteBody: String,
        createdTime: Long,
        modifiedTime: Long
    ) {
        viewModelScope.launch(IO) {
            repository.updateNote(
                Note(
                    noteTitle, noteBody, id = noteId, _createdTime = createdTime,
                    _modifiedTime = modifiedTime
                )
            )
        }
    }

    fun deleteNote(noteId: Int?) {
        viewModelScope.launch(IO) {
            repository.removeNote(noteId!!)
        }
    }

    companion object {
        private const val NOTE_TITLE = "noteTitle"
        private const val NOTE_BODY = "noteBody"
        private const val NOTE = "note"
    }
}