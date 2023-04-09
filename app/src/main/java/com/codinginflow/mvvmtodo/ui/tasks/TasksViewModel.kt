package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.codinginflow.mvvmtodo.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
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

    // 3 mutable live data for filters
    val searchQuery = MutableStateFlow("")
    // by default sorted by date
    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted = MutableStateFlow(false)

    // whenever value of searchQuery/sortOrder/hideCompleted changes (emits a new value)
    // execute block (update in Dao) and assign to tasksFlow
    private val tasksFlow = combine(
        searchQuery,
        sortOrder,
        hideCompleted
    // return latest values in Triple wrapper
    ) { query, sortOrder, hideCompleted ->
        Triple(query, sortOrder, hideCompleted)
    // use latest values to execute query in DAO
    }.flatMapLatest {(query, sortOrder, hideCompleted) ->
        taskDao.getTasks(query, sortOrder, hideCompleted)
    }
     // asLiveData is the latest value of the flow stream
    val tasks = tasksFlow.asLiveData()
}

// two distinct sorting states represented by single values
enum class SortOrder {BY_NAME, BY_DATE}