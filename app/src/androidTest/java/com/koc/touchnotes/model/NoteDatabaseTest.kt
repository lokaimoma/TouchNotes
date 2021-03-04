package com.koc.touchnotes.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.koc.touchnotes.enums.NoteSort
import com.koc.touchnotes.getOrAwaitValue
import com.koc.touchnotes.model.entities.Note
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
Created by kelvin_clark on 2/19/2021 2:41 PM
 */

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class NoteDatabaseTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var noteDatabase: NoteDatabase
    private lateinit var noteDao: NotesDao

    @Before
    fun setup() {
        noteDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NoteDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        noteDao = noteDatabase.getNotesDao()
    }

    @After
    fun tearDown() {
        noteDatabase.close()
    }

    @Test
    fun insertItemIntoDatabase() = runBlockingTest {
        val note = Note(
            "New note", "Note content",
            System.currentTimeMillis(), System.currentTimeMillis(), 1
        )

        noteDao.insertNote(note)

        val retrievedNote = noteDao.getNote(note.id)
        assertThat(retrievedNote).isEqualTo(note)
    }

    @Test
    fun deleteFromDatabase() = runBlockingTest {
        val note = Note(
            "New note", "Note content",
            System.currentTimeMillis(), System.currentTimeMillis(), 1
        )

        noteDao.insertNote(note)
        noteDao.removeNote(note.id)

        val retrievedNotes = noteDao.getNotes("", NoteSort.BY_CREATED_TIME)
            .asLiveData().getOrAwaitValue()

        assertThat(retrievedNotes).doesNotContain(note)
    }

    @Test
    fun updateItemInDatabase() = runBlockingTest {
        val note = Note(
            "New note", "Note content",
            System.currentTimeMillis(), System.currentTimeMillis(), 1
        )

        noteDao.insertNote(note)

        val noteUpdated = Note(
            "New note", "Updated body",
            System.currentTimeMillis(), System.currentTimeMillis(), 1
        )

        noteDao.updateNote(noteUpdated)

        val retrievedNote = noteDao.getNote(note.id)
        assertThat(retrievedNote).isEqualTo(noteUpdated)
    }
}