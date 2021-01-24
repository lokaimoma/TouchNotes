package com.koc.touchnotes.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koc.touchnotes.model.Note
import com.koc.touchnotes.model.NoteDatabase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

/**
Created by kelvin_clark on 12/20/2020
 */
class NoteEditViewModel @ViewModelInject constructor(private val notesDb: NoteDatabase) : ViewModel() {

    fun saveNote(noteTitle: String, noteBody: String, createdTime:Long, modifiedTime:Long) {
        viewModelScope.launch(IO) {
            if ((noteTitle != "") && (noteBody != "")){
                notesDb.getNotesDao().insertNote(Note(noteTitle, noteBody,createdTime, modifiedTime))
            }else if (noteTitle == ""){
                if (noteBody != ""){
                    notesDb.getNotesDao().insertNote(Note(body = noteBody, _createdTime = createdTime,
                        _modifiedTime = modifiedTime))
                }else{
                    notesDb.getNotesDao().insertNote(Note(_createdTime = createdTime,
                        _modifiedTime = modifiedTime))
                }
            }else if (noteBody == ""){
                if (noteTitle != ""){
                    notesDb.getNotesDao().insertNote(Note(title = noteTitle,_createdTime = createdTime,
                        _modifiedTime = modifiedTime))
                }else{
                    notesDb.getNotesDao().insertNote(Note(_createdTime = createdTime,
                        _modifiedTime = modifiedTime))
                }
            }else{
                notesDb.getNotesDao().insertNote(Note(_createdTime = createdTime,
                    _modifiedTime = modifiedTime))
            }
        }
    }

    fun updateNote(noteId: Int, noteTitle: String, noteBody: String, createdTime:Long, modifiedTime:Long){
        viewModelScope.launch(IO) {
            notesDb.getNotesDao().updateNote(Note(noteTitle, noteBody,id = noteId, _createdTime = createdTime,
                _modifiedTime = modifiedTime))
        }
    }

    fun deleteNote(noteId: Int?) {
        viewModelScope.launch(IO) {
            notesDb.getNotesDao().removeNote(noteId!!)
        }
    }
}