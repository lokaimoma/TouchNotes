package com.koc.touchnotes.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.koc.touchnotes.model.entities.TextSpan
import com.koc.touchnotes.util.Constants.IS_BOLD
import com.koc.touchnotes.util.Constants.IS_ITALIC
import com.koc.touchnotes.util.Constants.IS_STRIKE_THROUGH
import com.koc.touchnotes.util.Constants.IS_UNDERLINED
import com.koc.touchnotes.util.Constants.NOTE_ID
import com.koc.touchnotes.util.Constants.TEXT_END
import com.koc.touchnotes.util.Constants.TEXT_SPAN_TABLE
import com.koc.touchnotes.util.Constants.TEXT_START
import kotlinx.coroutines.flow.Flow

/**
Created by kelvin_clark on 3/4/2021 6:59 AM
 */
@Dao
interface TextSpanDao {

    @Query("SELECT $IS_BOLD, $IS_ITALIC, $IS_STRIKE_THROUGH, $IS_UNDERLINED, $TEXT_START, $TEXT_END, $NOTE_ID, id " +
            "FROM $TEXT_SPAN_TABLE WHERE $NOTE_ID = :noteId ;")
    fun getTextSpans(noteId: Int): Flow<List<TextSpan>>

    @Insert
    suspend fun insertTextSpan(textSpan: TextSpan)
}