package com.koc.touchnotes.view

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.koc.touchnotes.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}