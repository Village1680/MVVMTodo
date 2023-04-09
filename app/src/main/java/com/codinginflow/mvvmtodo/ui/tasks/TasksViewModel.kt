package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.codinginflow.mvvmtodo.data.TaskDao


// inject DAO to ViewModel
class TasksViewModel @ViewModelInject constructor(
    //define dependency
    private val taskDao: TaskDao
) : ViewModel() {
}