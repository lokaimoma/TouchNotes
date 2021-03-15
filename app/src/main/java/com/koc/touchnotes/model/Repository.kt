package com.koc.touchnotes.model

import com.koc.touchnotes.enums.NoteSort
import com.koc.touchnotes.model.dao.NotesDao
import com.koc.touchnotes.model.entities.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
Created by kelvin_clark on 2/19/2021 9:51 PM
 */
class Repository @Inject constructor(private val noteDao: NotesDao) : NoteRepository {
    override suspend fun insertNote(note: Note): Long {
        return noteDao.insertNote(note)
    }

    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    override suspend fun removeNote(id: Int) {
        noteDao.removeNote(id)
    }

    override suspend fun getNote(id: Int): Note {
        return noteDao.getNote(id)
    }

    override suspend fun getNotes(searchQuery: String, sortMethod: NoteSort): Flow<List<Note>> {
        return noteDao.getNotes(searchQuery, sortMethod)
    }
}