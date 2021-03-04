package com.koc.touchnotes.viewModel.ext

import com.koc.touchnotes.model.entities.Note

/**
Created by kelvin_clark on 2/20/2021 12:59 PM
 */
fun createNote(
    noteTitle: String,
    noteBody: String,
    createdTime: Long,
    modifiedTime: Long
    ): Note {
        return if (noteTitle != "" && noteBody != "") {
            Note(noteTitle, noteBody, createdTime, modifiedTime)
        }else if (noteTitle == "" && noteBody != ""){
            Note(body = noteBody, _createdTime = createdTime, _modifiedTime = modifiedTime)
        }else if(noteTitle != "" && noteBody == ""){
            Note(title = noteTitle, _createdTime = createdTime, _modifiedTime = modifiedTime)
        }
        else {
            Note(_createdTime = createdTime, _modifiedTime = modifiedTime)
        }
    }