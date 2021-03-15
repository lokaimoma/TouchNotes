package com.koc.touchnotes.util

import com.koc.touchnotes.enums.NoteLayout
import com.koc.touchnotes.model.entities.Note

/**
Created by kelvin_clark on 1/28/2021 10:48 PM
 */
sealed class NoteEvent {
    data class NoteClickedEvent(var note: Note) : NoteEvent()
    object AddNoteEvent : NoteEvent()
    data class NoteSwipedEvent(var note: Note) : NoteEvent()
    data class UpdateNoteLayoutStyleEvent(var layoutStyle: NoteLayout) : NoteEvent()
    object GotoSettingsScreen : NoteEvent()
}

sealed class NoteEditEvent {
    data class NoteSavedEvent(var id: Int) : NoteEditEvent()
    data class TextSpannedEvent(var textStart: Int, var textEnd: Int): NoteEditEvent()
}

val <T> T.exhaustive: T
    get() = this
