package com.koc.touchnotes.model


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

/**
Created by kelvin_clark on 12/5/2020
 */
@Parcelize
@Entity
data class Note(
    @ColumnInfo(name = "title")
    val title : String? = "New Note",
    @ColumnInfo(name = "body")
    val body : String? = "",
    @ColumnInfo(name = "createdTime")
    val _createdTime : Long?,
    @ColumnInfo(name = "modifiedTime")
    val _modifiedTime : Long?,
    @PrimaryKey(autoGenerate = true) val id :Int = 0
) : Parcelable