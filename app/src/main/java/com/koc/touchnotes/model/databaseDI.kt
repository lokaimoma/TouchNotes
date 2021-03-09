package com.koc.touchnotes.model

import android.app.Application
import androidx.room.Room
import com.koc.touchnotes.model.NoteDatabase.Companion.migrateFrom1To2
import com.koc.touchnotes.model.NoteDatabase.Companion.migrateFrom2To3
import com.koc.touchnotes.model.dao.NotesDao
import com.koc.touchnotes.model.dao.TextSpanDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
Created by kelvin_clark on 12/6/2020
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseDI {
    const val DATABASE_NAME = "Note_Database"

    @Provides
    @Singleton
    fun getDatabase(context: Application) =
        Room
            .databaseBuilder(
                context,
                NoteDatabase::class.java,
                DATABASE_NAME
            )
            .addMigrations(migrateFrom1To2, migrateFrom2To3)
            .build()

    @Provides
    @Singleton
    fun getNotesDao(database: NoteDatabase) = database.getNotesDao()

    @Provides
    @Singleton
    fun getTextSpanDao(database: NoteDatabase) = database.getTextSpanDao()

    @Provides
    fun getRepository(noteDao: NotesDao, spanDao: TextSpanDao) : NoteRepository {
        return Repository(noteDao, spanDao)
    }
}