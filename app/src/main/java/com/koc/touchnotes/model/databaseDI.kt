package com.koc.touchnotes.model

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

/**
Created by kelvin_clark on 12/6/2020
 */
@Module
@InstallIn(ActivityComponent::class)
object DatabaseDI {
    const val DATABASE_NAME = "Note_Database"

    @Provides
    fun getDatabase(@ActivityContext context: Context) : NoteDatabase {
        return Room
            .databaseBuilder(
                context,
                NoteDatabase::class.java,
                DATABASE_NAME
            )
            .build()
    }
}