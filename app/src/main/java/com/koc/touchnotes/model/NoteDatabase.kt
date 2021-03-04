package com.koc.touchnotes.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.koc.touchnotes.model.dao.NotesDao
import com.koc.touchnotes.model.dao.TextSpanDao
import com.koc.touchnotes.model.entities.Note
import com.koc.touchnotes.model.entities.TextSpan
import com.koc.touchnotes.util.Constants.IS_BOLD
import com.koc.touchnotes.util.Constants.IS_ITALIC
import com.koc.touchnotes.util.Constants.IS_STRIKE_THROUGH
import com.koc.touchnotes.util.Constants.IS_UNDERLINED
import com.koc.touchnotes.util.Constants.NOTE_ID
import com.koc.touchnotes.util.Constants.TEXT_END
import com.koc.touchnotes.util.Constants.TEXT_SPAN_TABLE
import com.koc.touchnotes.util.Constants.TEXT_START

/**
Created by kelvin_clark on 12/6/2020
 */
@Database(entities = [Note::class, TextSpan::class], version = 3, exportSchema = true)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun getNotesDao() : NotesDao
    abstract fun getTextSpanDao() : TextSpanDao

    companion object {
        var migrateFrom1To2 : Migration = object : Migration(1,2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Note ADD COLUMN createdTime BIGINT DEFAULT 0")
                database.execSQL("ALTER TABLE Note ADD COLUMN modifiedTime BIGINT DEFAULT 0")
            }
        }

        var migrateFrom2To3 = object : Migration(2,3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE $TEXT_SPAN_TABLE" +
                        "($IS_BOLD INTEGER NOT NULL, $IS_ITALIC INTEGER NOT NULL, " +
                        "$IS_STRIKE_THROUGH INTEGER NOT NULL, $IS_UNDERLINED INTEGER NOT NULL, " +
                        "$TEXT_START INTEGER NOT NULL, " +
                        "$TEXT_END INTEGER NOT NULL, FOREIGN KEY ($NOTE_ID) REFERENCES Note(Note.id) ON DELETE CASCADE ON UPDATE NO ACTION, " +
                        "$TEXT_SPAN_TABLE.id INTEGER PRIMARY KEY)")
            }

        }
    }
}