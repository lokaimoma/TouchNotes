package com.koc.touchnotes.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import com.koc.touchnotes.R
import com.koc.touchnotes.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kelvin_clark on 5/12/20
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.mainToolbar)
        setupActionBarWithNavController(findNavController(R.id.fragment))
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

    override fun onSupportNavigateUp() = findNavController(R.id.fragment).navigateUp()
            || super.onSupportNavigateUp()

}