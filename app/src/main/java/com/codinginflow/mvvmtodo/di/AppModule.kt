package com.codinginflow.mvvmtodo.di

import android.app.Application
import androidx.room.Room
import com.codinginflow.mvvmtodo.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

// instructions for dagger, how to create the dependencies we need (TaskDatabase, TaskDao)
@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton// only create one instance of our database
    // convention naming
    fun provideDatabase(
        app: Application,
        callback: TaskDatabase.Callback
        // = shorthand for return
        // creates/builds the database, destructiveMigration will drop the table and create a new one if updated db schema
    ) = Room.databaseBuilder(app, TaskDatabase::class.java, "task_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    fun provideTaskDao(db: TaskDatabase) = db.taskDao()
}