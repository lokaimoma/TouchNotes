package com.koc.touchnotes.model

import androidx.room.*
import com.koc.touchnotes.viewModel.NoteSort
import kotlinx.coroutines.flow.Flow

/**
Created by kelvin_clark on 12/5/2020
 */
@Dao
interface NotesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    fun updateNote(note: Note)

    @Query("DELETE FROM Note WHERE id = :id")
    suspend fun removeNote(id: Int)

    fun getNotes(searchQuery: String, sortMethod: NoteSort) : Flow<List<Note>> =
        when(sortMethod) {
            NoteSort.BY_TITLE -> sortByTitle(searchQuery)
            NoteSort.BY_MODIFIED_TIME -> sortByModifiedTime(searchQuery)
            NoteSort.BY_CREATED_TIME -> sortByCreatedTime(searchQuery)
        }

    @Query("SELECT * FROM Note WHERE title LIKE '%' || :searchQuery || '%' ORDER BY title")
    fun sortByTitle(searchQuery: String) : Flow<List<Note>>

    @Query("SELECT * FROM Note WHERE title LIKE '%' || :searchQuery || '%' ORDER BY createdTime")
    fun sortByCreatedTime(searchQuery: String) : Flow<List<Note>>

    @Query("SELECT * FROM Note WHERE title LIKE '%' || :searchQuery || '%' ORDER BY modifiedTime")
    fun sortByModifiedTime(searchQuery: String) : Flow<List<Note>>

    @Query("SELECT title FROM Note")
    fun getNoteTitles() : List<String>

    @Query("SELECT * FROM Note WHERE id = :id")
    fun getNote(id: Int) : Note
}