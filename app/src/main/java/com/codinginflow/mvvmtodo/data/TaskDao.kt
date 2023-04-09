package com.codinginflow.mvvmtodo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/*
    room library makes it easier to use SQLite in android apps
    provides compile-time safety by verifying queries
 */

// data access object
// DAO interface declares methods of crud operations
@Dao
interface TaskDao {
    /*
        suspend fun is a feature of coroutines and enforced by room, it is a function that can be
        paused and resumed on an independent thread (not main thread) without blocking other methods.

        Database operation can take a long time and will block ui updates/other tasks on the main
        thread and can introduce "lag".
     */

    fun getTasks(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
        when(sortOrder) {
            SortOrder.BY_DATE -> getTasksSortedDateCreated(query, hideCompleted)
            SortOrder.BY_NAME -> getTasksSortedByName(query, hideCompleted)
        }

    // % before and after lets us ":searchQuery" be at any location within task name
    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted or completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, name")
    // represents a stream of data, asynchronous stream data, continuously updated
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted or completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, date_in_millis")
    fun getTasksSortedDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    //room generates @Insert code
    //REPLACE when trying to update a task with a conflicting ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}