package com.koc.touchnotes.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.koc.touchnotes.databinding.ActivityMainBinding

/**
 * Created by kelvin_clark on 5/12/20
 */
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.mainToolbar)
    }
}