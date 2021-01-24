package com.koc.touchnotes.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.koc.touchnotes.model.NoteDatabase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext

/**
Created by kelvin_clark on 12/7/2020
 */
class NoteListViewModel @ViewModelInject constructor(private val notesDb : NoteDatabase): ViewModel(){

    val searchQuery = MutableStateFlow("")

    val noteSort = MutableStateFlow(NoteSort.BY_ID)

    private val noteListFlow = searchQuery.flatMapLatest { query ->
        notesDb.getNotesDao().getNotes(query)
    }

    fun getAllNotes() = noteListFlow.asLiveData()
}

enum class NoteSort{
    BY_TITLE,
    BY_CREATED_TIME,
    BY_MODIFIED_TIME,
    BY_ID
}