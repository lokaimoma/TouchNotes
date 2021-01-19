package com.koc.touchnotes.view

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.room.Room
import com.koc.touchnotes.R
import com.koc.touchnotes.model.DatabaseDI.DATABASE_NAME
import com.koc.touchnotes.model.Note
import com.koc.touchnotes.model.NoteDatabase

/**
Created by kelvin_clark on 1/19/2021 6:39 PM
 */
class NotesRemoteAdapter(val context: Context, private val intent: Intent?) :
    RemoteViewsService.RemoteViewsFactory {
    private val widgetItems = arrayListOf<Note>()
    private var widgetId: Int? = null
    private var database: NoteDatabase? = null

    override fun onCreate() {
        widgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        database = Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            DATABASE_NAME
        )
            .build()
    }

    override fun onDataSetChanged() {
        database?.getNotesDao()?.getNotes()?.let { widgetItems.addAll(it) }
    }

    override fun onDestroy() {
        widgetItems.clear()
    }

    override fun getCount(): Int {
        return widgetItems.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val remoteView = RemoteViews(context.packageName, R.layout.widget_item)
        remoteView.setTextViewText(R.id.noteTitle, widgetItems[position].title)

        val fillingIntent = Intent()
        fillingIntent.putExtra(NOTE_ID, widgetItems[position].id)

        remoteView.setOnClickFillInIntent(R.id.widgetItem, fillingIntent)

        return remoteView
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }


    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    companion object {
        const val NOTE_ID = "NoteID"
    }
}

class NotesService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return NotesRemoteAdapter(this.applicationContext, intent)
    }
}
