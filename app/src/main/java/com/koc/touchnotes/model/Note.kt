package com.koc.touchnotes.model


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
Created by kelvin_clark on 12/5/2020
 */
@Parcelize
@Entity
data class Note(
    @ColumnInfo(name = "title")
    var title : String? = "New Note",
    @ColumnInfo(name = "body")
    var body : String? = "",
    var createdTime : Long?,
    var modifiedTime : Long?,
    @PrimaryKey(autoGenerate = true) val id :Int = 0
) : Parcelable