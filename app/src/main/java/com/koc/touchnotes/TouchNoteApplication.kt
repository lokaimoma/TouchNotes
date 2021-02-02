package com.koc.touchnotes

import android.app.Application
import android.os.StrictMode
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
    }
}