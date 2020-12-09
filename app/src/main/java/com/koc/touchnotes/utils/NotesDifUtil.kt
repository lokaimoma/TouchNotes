package com.koc.touchnotes.utils

import androidx.recyclerview.widget.DiffUtil
import com.koc.touchnotes.model.Note
import javax.inject.Inject

/**
Created by kelvin_clark on 12/7/2020
 */
class NotesDifUtil @Inject constructor(): DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return (oldItem.title == newItem.title) && (oldItem.body == newItem.title)
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return (oldItem.title == newItem.title) && (oldItem.body == newItem.title)
    }
}