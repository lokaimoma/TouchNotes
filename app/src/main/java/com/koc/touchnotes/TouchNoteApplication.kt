package com.koc.touchnotes

import android.app.Application
import android.os.StrictMode
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp

/**
Created by kelvin_clark on 12/5/2020
 */
@HiltAndroidApp
class TouchNoteApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
        }
        applySelectedTheme()
    }

    private fun applySelectedTheme() {
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)

        when (sharedPreference.getString(getString(R.string.theme_key), "")) {
            getString(R.string.system_default_theme) -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

            getString(R.string.light_theme) -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO)

            getString(R.string.dark_theme) -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}