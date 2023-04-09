package com.codinginflow.mvvmtodo.ui.tasks

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.codinginflow.mvvmtodo.R
import dagger.hilt.android.AndroidEntryPoint

// annotation lets dagger know to inject here
@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {
    // inject viewmodel into fragment
    private val viewModel: TasksViewModel by viewModels()
}