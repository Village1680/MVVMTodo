package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.codinginflow.mvvmtodo.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

/*
    viewmodel should never reference a activity/fragment as the view model lives much longer (eg fragment destruction on orientation change)
    this would create a memory leak when the fragment is destroyed.
 */

// inject DAO to ViewModel
class TasksViewModel @ViewModelInject constructor(
    //define dependency
    private val taskDao: TaskDao
) : ViewModel() {
    //
    val searchQuery = MutableStateFlow("")

    // whenever value of searchQuery changes, execute block (update query in Dao) and assign to tasksFlow
    private val tasksFlow = searchQuery.flatMapLatest {
        taskDao.getTasks(it)
    }
     // asLiveData is the latest value of the flow stream
    val tasks = tasksFlow.asLiveData()
}