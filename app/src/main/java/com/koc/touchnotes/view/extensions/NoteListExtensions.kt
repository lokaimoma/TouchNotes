package com.koc.touchnotes.view.extensions

import androidx.appcompat.widget.SearchView

/**
Created by kelvin_clark on 1/24/2021 6:33 PM
 */

inline fun SearchView.queryTextListener(
    crossinline func: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener{

        override fun onQueryTextSubmit(query: String?): Boolean {
            return true //
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            func(newText.orEmpty())
            return true
        }
    })
}