package com.codinginflow.mvvmtodo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codinginflow.mvvmtodo.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint //inject here because this activity displays fragments
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}