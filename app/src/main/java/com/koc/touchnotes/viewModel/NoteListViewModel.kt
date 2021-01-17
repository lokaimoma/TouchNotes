package com.koc.touchnotes.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.koc.touchnotes.model.NoteDatabase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
Created by kelvin_clark on 12/7/2020
 */
class NoteListViewModel @Inject constructor(): ViewModel(){
    @Inject lateinit var notesDb : NoteDatabase

    fun getAllNotes() = liveData {
        withContext(viewModelScope.coroutineContext + IO) {
            notesDb.getNotesDao().getNotes()
        }.let {
            emit(it)
        }
    }
}