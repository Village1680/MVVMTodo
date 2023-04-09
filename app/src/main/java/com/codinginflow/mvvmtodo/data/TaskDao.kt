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

    @Query("SELECT * FROM task_table")

    // represents a stream of data, asynchronous stream data, continuously updated
    fun getTasks(): Flow<List<Task>>

    //room generates @Insert code
    //REPLACE when trying to update a task with a conflicting ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}