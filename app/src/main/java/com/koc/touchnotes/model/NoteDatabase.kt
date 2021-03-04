package com.koc.touchnotes.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.koc.touchnotes.model.dao.NotesDao
import com.koc.touchnotes.model.entities.Note

/**
Created by kelvin_clark on 12/6/2020
 */
@Database(entities = [Note::class], version = 2, exportSchema = true)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun getNotesDao() : NotesDao

    companion object {
        var migrateFrom1To2 : Migration = object : Migration(1,2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Note ADD COLUMN createdTime BIGINT DEFAULT 0")
                database.execSQL("ALTER TABLE Note ADD COLUMN modifiedTime BIGINT DEFAULT 0")
            }
        }
    }
}