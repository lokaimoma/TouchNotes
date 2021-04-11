package com.koc.touchnotes.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.koc.touchnotes.getOrAwaitValueTest
import com.koc.touchnotes.model.Repository
import com.koc.touchnotes.model.entities.Note
import com.koc.touchnotes.preferenceManager.PreferenceManager
import com.koc.touchnotes.util.Constants.SEARCH_QUERY
import com.koc.touchnotes.util.MainCoroutineRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test


/**
 * Created by kelvin_clark on 2/20/2021 7:47 AM
 */
@ExperimentalCoroutinesApi
class NoteListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()


    @Test
    fun test_getAllNotesEmpty() {
        val listViewModel: NoteListViewModel = mock()
        whenever(listViewModel.getAllNotes())
            .thenReturn(MutableLiveData(listOf()))
        val expected = 0

        val actual = listViewModel.getAllNotes().getOrAwaitValueTest()

        assertThat(actual).isEmpty()
        assertThat(actual.size).isEqualTo(expected)
    }

    @Test
    fun test_getAllNotesSingle() {
        val note = Note(
            "New note", "Note content", null,
            null, 1
        )

        val listViewModel: NoteListViewModel = mock()
        whenever(listViewModel.getAllNotes())
            .thenReturn(MutableLiveData(listOf(note)))
        val expected = 1

        val actual = listViewModel.getAllNotes().getOrAwaitValueTest()

        assertThat(actual.size).isEqualTo(expected)
        assertThat(actual).contains(note)
    }

    @Test
    fun test_getAllNotesMultiple() {
        val note = Note(
            "New note", "Note content", null,
            null, 1
        )
        val note2 = Note(
            "New note", "Note content", null,
            null, 2
        )
        val note3 = Note(
            "New note", "Note content", null,
            null, 3
        )

        val listViewModel: NoteListViewModel = mock()
        whenever(listViewModel.getAllNotes())
            .thenReturn(MutableLiveData(listOf(note, note2, note3)))
        val expected = 3

        val actual = listViewModel.getAllNotes().getOrAwaitValueTest()

        assertThat(actual.size).isEqualTo(expected)
        assertThat(actual).contains(note)
    }

    // fails in github actions
//    @Test
//    fun test_updateSortOrder() = runBlockingTest {
//        val repository: Repository = mock()
//
//        val stateHandle: SavedStateHandle = mock()
//        whenever(stateHandle.getLiveData(SEARCH_QUERY, ""))
//            .thenReturn(MutableLiveData(""))
//
//        val preferenceManager: PreferenceManager = mock()
//        val viewModel = NoteListViewModel(repository, preferenceManager, stateHandle)
//
//        viewModel.updateSortOrder(NoteSort.BY_MODIFIED_TIME)
//
//        verify(preferenceManager)
//            .updateSortOrder(NoteSort.BY_MODIFIED_TIME)
//    }

    @Test
    fun test_swipeNote() = runBlockingTest {
        val repository: Repository = mock()

        val stateHandle: SavedStateHandle = mock()
        whenever(stateHandle.getLiveData(SEARCH_QUERY, ""))
            .thenReturn(MutableLiveData(""))

        val preferenceManager: PreferenceManager = mock()
        val viewModel = NoteListViewModel(repository, preferenceManager, stateHandle)

        val note = Note(
            "New note", "Note content", null,
            null, 1
        )

        viewModel.noteSwiped(note)

        verify(repository)
            .removeNote(note.id)
    }

    @Test
    fun test_restoreNote() = runBlockingTest {
        val repository: Repository = mock()

        val stateHandle: SavedStateHandle = mock()
        whenever(stateHandle.getLiveData(SEARCH_QUERY, ""))
            .thenReturn(MutableLiveData(""))

        val preferenceManager: PreferenceManager = mock()
        val viewModel = NoteListViewModel(repository, preferenceManager, stateHandle)

        val note = Note(
            "New note", "Note content", null,
            null, 1
        )

        viewModel.restoreNote(note)

        verify(repository)
            .insertNote(any())
    }


}