package com.koc.touchnotes.viewModel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.koc.touchnotes.enums.NoteLayout
import com.koc.touchnotes.enums.NoteSort
import com.koc.touchnotes.model.Note
import com.koc.touchnotes.model.NoteDatabase
import com.koc.touchnotes.preferenceManager.PreferenceManager
import com.koc.touchnotes.util.NoteEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


/**
Created by kelvin_clark on 12/7/2020
 */
class NoteListViewModel @ViewModelInject constructor(
    private val notesDb: NoteDatabase,
    private val preferenceManager: PreferenceManager,
    @Assisted val state: SavedStateHandle
) : ViewModel() {

    var layoutStyle : NoteLayout? = null

    val searchQuery = state.getLiveData(SEARCH_QUERY,"")
    private val noteEventChannel = Channel<NoteEvent>()
    val noteEvent = noteEventChannel.receiveAsFlow()

    private val noteSort = preferenceManager.sortPreferencesFlow

    val noteLayoutStyle = preferenceManager.layoutPreferenceFlow

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    private val noteListFlow = combine(
        searchQuery.asFlow(),
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

    fun noteSwiped(note: Note) = viewModelScope.launch {
        notesDb.getNotesDao().removeNote(note.id)
        noteEventChannel.send(NoteEvent.NoteSwipedEvent(note))
    }

    fun restoreNote(note: Note) = viewModelScope.launch {
        notesDb.getNotesDao().insertNote(note.copy(id = 0,_createdTime = System.currentTimeMillis(),
        _modifiedTime = System.currentTimeMillis()))
    }

    fun updateNoteLayoutStyle(layoutStyle: NoteLayout) = viewModelScope.launch {
        preferenceManager.updateLayoutStyle(layoutStyle)
        noteEventChannel.send(NoteEvent.UpdateNoteLayoutStyleEvent(layoutStyle))
    }

    fun collectNoteLayoutStyle() = viewModelScope.launch(IO) {
        layoutStyle = noteLayoutStyle.first()
    }

    companion object{
        private const val SEARCH_QUERY = "search_query"
    }
}