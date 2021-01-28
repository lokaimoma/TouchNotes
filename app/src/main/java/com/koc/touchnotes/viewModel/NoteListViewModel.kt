package com.koc.touchnotes.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.koc.touchnotes.enums.NoteSort
import com.koc.touchnotes.model.Note
import com.koc.touchnotes.model.NoteDatabase
import com.koc.touchnotes.preferenceManager.PreferenceManager
import com.koc.touchnotes.util.NoteEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


/**
Created by kelvin_clark on 12/7/2020
 */
class NoteListViewModel @ViewModelInject constructor(
    private val notesDb: NoteDatabase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    private val noteEventChannel = Channel<NoteEvent>()
    val noteEvent = noteEventChannel.receiveAsFlow()

    private val noteSort = preferenceManager.sortPreferencesFlow

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    private val noteListFlow = combine(
        searchQuery,
        noteSort
    ) { searchQuery, noteSort ->
        Pair(searchQuery, noteSort)
    }.flatMapLatest { (query, sortMethod) ->
        notesDb.getNotesDao().getNotes(query, sortMethod)
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun getAllNotes() = noteListFlow.asLiveData()

    fun updateSortOrder(sortOrder: NoteSort) = viewModelScope.launch(IO) {
        preferenceManager.updateSortOrder(sortOrder)
    }

    fun noteClicked(note: Note)= viewModelScope.launch {
        noteEventChannel.send(NoteEvent.NoteClickedEvent(note))
    }

    fun addNoteClicked()= viewModelScope.launch {
        noteEventChannel.send(NoteEvent.AddNoteEvent)
    }
}