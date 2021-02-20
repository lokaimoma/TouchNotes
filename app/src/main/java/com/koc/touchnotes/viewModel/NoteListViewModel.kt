package com.koc.touchnotes.viewModel

import androidx.lifecycle.*
import com.koc.touchnotes.enums.NoteLayout
import com.koc.touchnotes.enums.NoteSort
import com.koc.touchnotes.model.Note
import com.koc.touchnotes.model.NoteDatabase
import com.koc.touchnotes.model.Repository
import com.koc.touchnotes.preferenceManager.PreferenceManager
import com.koc.touchnotes.util.NoteEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
Created by kelvin_clark on 12/7/2020
 */
@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val repository: Repository,
    private val preferenceManager: PreferenceManager,
    state: SavedStateHandle
) : ViewModel() {

    var layoutStyle : NoteLayout? = null

    val searchQuery = state.getLiveData(SEARCH_QUERY,"")
    private val noteEventChannel = Channel<NoteEvent>()
    val noteEvent = noteEventChannel.receiveAsFlow()

    private val noteSort = preferenceManager.sortPreferencesFlow

    private val noteLayoutStyle = preferenceManager.layoutPreferenceFlow

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

    fun updateNoteLayoutStyle(layoutStyle: NoteLayout) = viewModelScope.launch {
        preferenceManager.updateLayoutStyle(layoutStyle)
        noteEventChannel.send(NoteEvent.UpdateNoteLayoutStyleEvent(layoutStyle))
    }

    fun collectNoteLayoutStyle() = viewModelScope.launch(IO) {
        layoutStyle = noteLayoutStyle.first()
    }

    fun requestSettingsScreen() = viewModelScope.launch {
        noteEventChannel.send(NoteEvent.GotoSettingsScreen)
    }

    companion object{
        private const val SEARCH_QUERY = "search_query"
    }
}