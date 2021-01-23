package com.koc.touchnotes.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.koc.touchnotes.model.NoteDatabase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

/**
Created by kelvin_clark on 12/7/2020
 */
class NoteListViewModel @ViewModelInject constructor(private val notesDb : NoteDatabase): ViewModel(){

    fun getAllNotes() = liveData {
        withContext(viewModelScope.coroutineContext + IO) {
            notesDb.getNotesDao().getNotes()
        }.let {
            emit(it)
        }
    }
}