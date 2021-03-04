package com.koc.touchnotes.model.dao

import androidx.room.Dao
import androidx.room.Query
import com.koc.touchnotes.model.entities.TextSpan
import kotlinx.coroutines.flow.Flow

/**
Created by kelvin_clark on 3/4/2021 6:59 AM
 */
@Dao
interface TextSpanDao {

    @Query("SELECT ")
    fun getTextSpans(noteId: Int): Flow<List<TextSpan>>
}