package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.codinginflow.mvvmtodo.data.PreferencesManager
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/*
    viewmodel should never reference a activity/fragment as the view model lives much longer (eg fragment destruction on orientation change)
    this would create a memory leak when the fragment is destroyed.
 */

// inject DAO to ViewModel
class TasksViewModel @ViewModelInject constructor(
    //define dependencies to inject
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    // 3 mutable live data for filters
    val searchQuery = MutableStateFlow("")


    // by default sorted by date
    val preferencesFlow = preferencesManager.preferencesFlow

    // as a channel, the fragment could put something into the channel
    private val tasksEventChannel = Channel<TasksEvent>()
    // turn into a flow, so we can consume single values out
    val tasksEvent = tasksEventChannel.receiveAsFlow()

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
    // asLiveData is the latest value of the flow stream
    val tasks = tasksFlow.asLiveData()

    // define functionalities on click/select
    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(task: Task) {}

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    // represent events to send to the fragment
    // restricted class similar to enum, but allows for multiple types
    // compiler knows only the tasks defined in here, so it will give a warning if task not within sealed class
    sealed class TasksEvent {
        data class ShowUndoDeleteTaskMessage(val task: Task) : TasksEvent()
    }
}
