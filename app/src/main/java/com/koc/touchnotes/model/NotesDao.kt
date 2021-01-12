package com.koc.touchnotes.model

import androidx.room.*

/**
Created by kelvin_clark on 12/5/2020
 */
@Dao
interface NotesDao {
    @Insert
    fun insertNote(note: Note)

    @Update(onConflict =  OnConflictStrategy.REPLACE)
    fun updateNote(note: Note)

    @Query("DELETE FROM Note WHERE id = :id")
    fun removeNote(id: Int)

    @Query("SELECT * FROM Note")
    fun getNotes() : List<Note>

    @Query("SELECT * FROM Note WHERE id = :id")
    fun getNote(id: Int) : Note
}