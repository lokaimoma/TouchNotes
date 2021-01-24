package com.koc.touchnotes.model

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.koc.touchnotes.model.NoteDatabase.Companion.migrateFrom1To2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Singleton

/**
Created by kelvin_clark on 12/6/2020
 */
@Module
@InstallIn(ApplicationComponent::class)
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
            .addMigrations(migrateFrom1To2)
            .build()
}