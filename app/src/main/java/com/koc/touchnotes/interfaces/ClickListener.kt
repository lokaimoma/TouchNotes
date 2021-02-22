package com.koc.touchnotes.interfaces

import com.koc.touchnotes.model.Note

/**
Created by kelvin_clark on 1/28/2021 10:34 PM
 */
interface ClickListener {
    fun onClickListener(note: Note, position: Int)
}