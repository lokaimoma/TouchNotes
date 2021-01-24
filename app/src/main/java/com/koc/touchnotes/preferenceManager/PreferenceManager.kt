package com.koc.touchnotes.preferenceManager

import android.content.Context
import androidx.datastore.preferences.createDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
Created by kelvin_clark on 1/24/2021 8:50 PM
 */

@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.createDataStore(PREFERENCE_NAME)

    val preferencesFlow = dataStore.data
        .map {

        }

    companion object {
        private const val PREFERENCE_NAME = "user_preference"
    }
}