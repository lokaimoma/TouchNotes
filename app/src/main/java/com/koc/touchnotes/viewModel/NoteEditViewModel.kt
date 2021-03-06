package com.koc.touchnotes.viewModel

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.print.PDFPrint
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koc.touchnotes.model.NoteRepository
import com.koc.touchnotes.model.entities.Note
import com.koc.touchnotes.model.entities.TextSpan
import com.koc.touchnotes.util.Constants.IS_BOLD
import com.koc.touchnotes.util.Constants.IS_ITALIC
import com.koc.touchnotes.util.Constants.IS_STRIKE_THROUGH
import com.koc.touchnotes.util.Constants.IS_UNDERLINED
import com.koc.touchnotes.util.Constants.NOTE
import com.koc.touchnotes.util.Constants.NOTE_BODY
import com.koc.touchnotes.util.Constants.NOTE_TITLE
import com.koc.touchnotes.util.NoteEditEvent
import com.koc.touchnotes.viewModel.ext.createNote
import com.tejpratapsingh.pdfcreator.utils.PDFUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

/**
Created by kelvin_clark on 12/20/2020
 */
@HiltViewModel
class NoteEditViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val noteState: SavedStateHandle
) : ViewModel() {

    val TAG = NoteEditViewModel::class.java.simpleName
    private val _noteEditChannel = Channel<NoteEditEvent>()
    val noteEditEvent = _noteEditChannel.receiveAsFlow()

    val note = noteState.get<Note>(NOTE)

    var title = noteState.get<String>(NOTE_TITLE) ?: note?.title ?: ""
        set(value) {
            field = value
            noteState.set(NOTE_TITLE, value)
        }

    var body = SpannableStringBuilder(noteState.get<String>(NOTE_BODY) ?: note?.body ?: "")
        set(value) {
            field = value
            noteState.set(NOTE_BODY, value.toString())
        }

    fun saveNote(
        noteTitle: String, noteBody: String, createdTime: Long, modifiedTime: Long,
        onComplete: (() -> Unit)?
    ) {
        viewModelScope.launch {
            val note = createNote(noteTitle, noteBody, createdTime, modifiedTime)
            val noteId: Long?
            noteId = withContext(IO) { repository.insertNote(note) }
            onComplete?.invoke()
                ?: _noteEditChannel.send(NoteEditEvent.NoteSavedEvent(noteId.toInt()))
        }
    }

    fun updateNote(
        noteId: Int,
        noteTitle: String,
        noteBody: String,
        createdTime: Long,
        modifiedTime: Long,
        onComplete: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            if (noteTitle != "" && noteBody != "") {
                withContext(IO) {
                    repository.updateNote(
                        Note(
                            noteTitle, noteBody, id = noteId, _createdTime = createdTime,
                            _modifiedTime = modifiedTime
                        )
                    )
                }
            }
            onComplete?.invoke()
        }
    }

    fun deleteNote(noteId: Int?) {
        viewModelScope.launch(IO) {
            repository.removeNote(noteId!!)
        }
    }

    fun processNoteSpans() = viewModelScope.launch {
        if (note?.id != null) {
            repository.getTextSpans(note.id).first { textSpans ->
                textSpans.forEach { textSpan ->
                    when {
                        textSpan.isBold -> {
                            applySpan(StyleSpan(Typeface.BOLD), textSpan.textStart, textSpan.textEnd)
                        }
                        textSpan.isItalic -> {
                            applySpan(StyleSpan(Typeface.ITALIC), textSpan.textStart, textSpan.textEnd)
                        }
                        textSpan.isUnderlined -> {
                            applySpan(UnderlineSpan(), textSpan.textStart, textSpan.textEnd)
                        }
                        textSpan.isStrikeThrough -> {
                            applySpan(StrikethroughSpan(), textSpan.textStart, textSpan.textEnd)
                        }
                    }
                }
                return@first true
            }
            _noteEditChannel.send(NoteEditEvent.TextSpannedEvent(0, 0))
        }
    }

    private fun applySpan(styleSpan: Any, textStart: Int, textEnd: Int) {
        body.setSpan(styleSpan, textStart, textEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    fun applySpan(noteId: Int, spanName: String, span: Any, textStart: Int, textEnd: Int) =
        viewModelScope.launch {
            if ((textStart != -1) && (textEnd != -1)) {
                body.setSpan(span, textStart, textEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                _noteEditChannel.send(NoteEditEvent.TextSpannedEvent(textStart, textEnd))
                saveSpan(noteId, spanName, textStart, textEnd)
            }
        }

    private suspend fun saveSpan(noteId: Int, spanName: String, textStart: Int, textEnd: Int) =
        withContext(IO) {
            when (spanName) {
                IS_BOLD -> repository.insertTextSpan(
                    TextSpan(
                        noteId = noteId,
                        isBold = true, textStart = textStart, textEnd = textEnd
                    )
                )

                IS_ITALIC -> repository.insertTextSpan(
                    TextSpan(
                        noteId = noteId,
                        isItalic = true, textStart = textStart, textEnd = textEnd
                    )
                )

                IS_UNDERLINED -> repository.insertTextSpan(
                    TextSpan(
                        noteId = noteId,
                        isUnderlined = true, textStart = textStart, textEnd = textEnd
                    )
                )

                IS_STRIKE_THROUGH -> repository.insertTextSpan(
                    TextSpan(
                        noteId = noteId,
                        isStrikeThrough = true, textStart = textStart, textEnd = textEnd
                    )
                )
            }
        }

    fun generatePDF(context: Context, createdFileUri: Uri) {
        val file = File(getPDFDir(context), "$title.pdf")
        PDFUtil.generatePDFFromHTML(
            context,
            file,
            generateHTML(),
            object : PDFPrint.OnPDFPrintListener {
                override fun onSuccess(file: File?) {

                    try {
                        val contentResolver = context.contentResolver

                        contentResolver.openFileDescriptor(createdFileUri, "w").use {
                            FileOutputStream(it?.fileDescriptor).use { fileOutputStream ->
                                fileOutputStream.write(file?.readBytes())
                            }
                        }
                    } catch (exception: IOException) {
                        Log.e(TAG, "onSuccess: Error Writing To Created File", exception)
                    }

                    viewModelScope.launch {
                        _noteEditChannel.send(NoteEditEvent.PDFCreatedEvent(createdFileUri))
                    }

                    try {
                        file?.delete()
                    } catch (exception: IOException) {
                        Log.e(TAG, "generatePDF: Error Deleting File", exception)
                    }
                }


                override fun onError(exception: Exception?) {
                    exception?.printStackTrace()
                }

            })

    }

    private fun getPDFDir(context: Context): String? {
        val pdfDir = File("${context.filesDir}${File.separator}pdf")
        if (!pdfDir.exists())
            pdfDir.mkdir()
        return pdfDir.absolutePath
    }

    private fun generateHTML(): String {
        return " <!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\n" +
                "<h1>$title</h1>\n" +
                "<p>$body</p>\n" +
                "\n" +
                "</body>\n" +
                "</html> "
    }
}