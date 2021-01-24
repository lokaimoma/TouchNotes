package com.koc.touchnotes.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.koc.touchnotes.model.NoteDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest


/**
Created by kelvin_clark on 12/7/2020
 */
class NoteListViewModel @ViewModelInject constructor(private val notesDb : NoteDatabase): ViewModel(){

    val searchQuery = MutableStateFlow("")

    val noteSort = MutableStateFlow(NoteSort.BY_CREATED_TIME)

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    private val noteListFlow = combine(
        searchQuery,
        noteSort
    ){searchQuery, noteSort ->
        Pair(searchQuery, noteSort)
    }.flatMapLatest { (query, sortMethod) ->
        notesDb.getNotesDao().getNotes(query, sortMethod)
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun getAllNotes() = noteListFlow.asLiveData()
}

enum class NoteSort{
    BY_TITLE,
    BY_CREATED_TIME,
    BY_MODIFIED_TIME
}