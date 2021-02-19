package com.koc.touchnotes.model

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
Created by kelvin_clark on 2/19/2021 2:41 PM
 */

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class NoteDatabaseTest {

    private lateinit var noteDatabase: NoteDatabase
    private lateinit var noteDao: NotesDao

    @Before
    fun setup() {
        noteDatabase = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NoteDatabase::class.java,
            "NotesDb"
        ).build()
        noteDao = noteDatabase.getNotesDao()
    }

    @After
    fun tearDown() {
        noteDatabase.close()
    }

    @Test
    fun insertItemIntoDatabase() = runBlockingTest {
        val note = Note("New note", "Note content",
        System.currentTimeMillis(), System.currentTimeMillis(), 1)

        noteDao.insertNote(note)
    }
}