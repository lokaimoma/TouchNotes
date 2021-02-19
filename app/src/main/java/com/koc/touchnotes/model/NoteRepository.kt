package com.koc.touchnotes.model

import com.koc.touchnotes.enums.NoteSort
import kotlinx.coroutines.flow.Flow


/**
Created by kelvin_clark on 2/19/2021 9:47 PM
 */
interface NoteRepository {
    suspend fun insertNote(note: Note) : Long
    suspend fun updateNote(note: Note)
    suspend fun removeNote(id: Int)
    suspend fun getNote(id: Int) : Note
    suspend fun getNotes(searchQuery: String, sortOrder: NoteSort) : Flow<List<Note>>
}