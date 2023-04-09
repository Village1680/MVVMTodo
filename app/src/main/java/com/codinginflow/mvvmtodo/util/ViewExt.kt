package com.codinginflow.mvvmtodo.util

import androidx.appcompat.widget.SearchView

// creating an extention method for SearchView more suitable for use-case, dont need onQueryTextSubmit functionality in app
// crossinline stops from calling return when utilising onQueryTextChanged.
inline fun SearchView.onQueryTextChanged(crossinline  listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

        override fun onQueryTextSubmit(query: String?): Boolean {
            // do nothing
            return true
        }

        // update list as characters entered into search
        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}