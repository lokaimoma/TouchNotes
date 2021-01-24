package com.koc.touchnotes.model

import androidx.room.*
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

    @Query("SELECT * FROM Note")
    fun getNotes() : Flow<List<Note>>

    @Query("SELECT title FROM Note")
    fun getNoteTitles() : List<String>

    @Query("SELECT * FROM Note WHERE id = :id")
    fun getNote(id: Int) : Note
}