package com.koc.touchnotes.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
Created by kelvin_clark on 12/5/2020
 */
@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id :Int,
    var title : String,
    var body : String
)