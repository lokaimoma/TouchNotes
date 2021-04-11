package com.koc.touchnotes.viewModel

import androidx.lifecycle.*
import com.koc.touchnotes.enums.NoteSort
import com.koc.touchnotes.model.NoteRepository
import com.koc.touchnotes.model.entities.Note
import com.koc.touchnotes.preferenceManager.PreferenceManager
import com.koc.touchnotes.util.Constants.RECYCLER_VIEW_POSITION
import com.koc.touchnotes.util.Constants.SEARCH_QUERY
import com.koc.touchnotes.util.NoteEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
Created by kelvin_clark on 12/7/2020
 */
@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val preferenceManager: PreferenceManager,
    state: SavedStateHandle
) : ViewModel() {
    var lastRecyclerViewPosition = state.get(RECYCLER_VIEW_POSITION) ?: 0

    val searchQuery = state.getLiveData(SEARCH_QUERY,"")
    private val noteEventChannel = Channel<NoteEvent>()
    val noteEvent = noteEventChannel.receiveAsFlow()

    private val noteSort = preferenceManager.sortPreferencesFlow

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    private val noteListFlow = combine(
        searchQuery.asFlow(),
        noteSort
    ) { searchQuery, noteSort ->
        Pair(searchQuery, noteSort)
    }.flatMapLatest { (query, sortMethod) ->
        repository.getNotes(query, sortMethod)
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
        repository.removeNote(note.id)
        noteEventChannel.send(NoteEvent.NoteSwipedEvent(note))
    }

    fun restoreNote(note: Note) = viewModelScope.launch {
        repository.insertNote(note.copy(id = 0,_createdTime = System.currentTimeMillis(),
        _modifiedTime = System.currentTimeMillis()))
    }

    fun requestSettingsScreen() = viewModelScope.launch {
        noteEventChannel.send(NoteEvent.GotoSettingsScreen)
    }
}