package com.koc.touchnotes.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koc.touchnotes.model.Note
import com.koc.touchnotes.model.NoteDatabase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
Created by kelvin_clark on 12/20/2020
 */
class NoteEditViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var notesDb: NoteDatabase

    fun saveNote(noteTitle: String, noteBody: String) {
        viewModelScope.launch(IO) {
            if ((noteTitle != "") && (noteBody != "")){
                notesDb.getNotesDao().insertNote(Note(noteTitle, noteBody))
            }else if (noteTitle == ""){
                if (noteBody != ""){
                    notesDb.getNotesDao().insertNote(Note(body = noteBody))
                }else{
                    notesDb.getNotesDao().insertNote(Note())
                }
            }else if (noteBody == ""){
                if (noteTitle != ""){
                    notesDb.getNotesDao().insertNote(Note(title = noteTitle))
                }else{
                    notesDb.getNotesDao().insertNote(Note())
                }
            }else{
                notesDb.getNotesDao().insertNote(Note())
            }
        }
    }
}