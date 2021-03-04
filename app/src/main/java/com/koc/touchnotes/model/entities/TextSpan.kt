package com.koc.touchnotes.model.entities

import androidx.room.*
import com.koc.touchnotes.util.Constants.IS_BOLD
import com.koc.touchnotes.util.Constants.IS_ITALIC
import com.koc.touchnotes.util.Constants.IS_STRIKE_THROUGH
import com.koc.touchnotes.util.Constants.IS_UNDERLINED
import com.koc.touchnotes.util.Constants.NOTE_ID
import com.koc.touchnotes.util.Constants.TEXT_END
import com.koc.touchnotes.util.Constants.TEXT_SPAN_TABLE
import com.koc.touchnotes.util.Constants.TEXT_START

/**
Created by kelvin_clark on 3/4/2021 6:46 AM
 */
@Entity(
    tableName = TEXT_SPAN_TABLE,
    foreignKeys = [ForeignKey(
        entity = Note::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf(NOTE_ID),
        onDelete = ForeignKey.CASCADE
    )]
)
data class TextSpan(
    @ColumnInfo(name = IS_BOLD)
    val isBold: Boolean = false,
    @ColumnInfo(name = IS_ITALIC)
    val isItalic: Boolean = false,
    @ColumnInfo(name = IS_STRIKE_THROUGH)
    val isStrikeThrough: Boolean = false,
    @ColumnInfo(name = IS_UNDERLINED)
    val isUnderlined: Boolean = false,
    @ColumnInfo(name = TEXT_START)
    val textStart: Int,
    @ColumnInfo(name = TEXT_END)
    val textEnd: Int,
    @ColumnInfo(name = NOTE_ID, index = true)
    val noteId: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)


