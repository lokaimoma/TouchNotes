package com.koc.touchnotes.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
Created by kelvin_clark on 3/4/2021 6:46 AM
 */
@Entity(
    foreignKeys = [ForeignKey(
        entity = Note::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("noteId"),
        onDelete = ForeignKey.CASCADE)]
)
data class TextSpan(
    val isBold: Boolean,
    val isItalic: Boolean,
    val isStrikeThrough: Boolean,
    val isUnderlined: Boolean,
    val textStart: Int,
    val textEnd: Int,
    val noteId: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
