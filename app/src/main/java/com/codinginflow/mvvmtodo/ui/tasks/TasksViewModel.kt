package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.codinginflow.mvvmtodo.data.PreferencesManager
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

/*
    viewmodel should never reference a activity/fragment as the view model lives much longer (eg fragment destruction on orientation change)
    this would create a memory leak when the fragment is destroyed.
 */

// inject DAO to ViewModel
class TasksViewModel @ViewModelInject constructor(
    //define dependencies to inject
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // 3 mutable live data for filters
    val searchQuery = MutableStateFlow("")
    // by default sorted by date
    val preferencesFlow = preferencesManager.preferencesFlow

    // whenever value of searchQuery/sortOrder/hideCompleted changes (emits a new value)
    // execute block (update in Dao) and assign to tasksFlow
    private val tasksFlow = combine(
        searchQuery,
        preferencesFlow
    // return latest values in Triple wrapper
    ) { query, preferences ->
        Pair(query, preferences)
    // use latest values to execute query in DAO
    }.flatMapLatest {(query, preferences) ->
        taskDao.getTasks(query, preferences.sortOrder, preferences.hideCompleted)
    }

    // functionality for fragment to update values in preferences manager
    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    // asLiveData is the latest value of the flow stream
    val tasks = tasksFlow.asLiveData()
}
