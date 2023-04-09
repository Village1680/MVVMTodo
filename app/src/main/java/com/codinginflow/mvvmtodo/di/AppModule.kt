package com.codinginflow.mvvmtodo.di

import android.app.Application
import androidx.room.Room
import com.codinginflow.mvvmtodo.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
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

    /*
        coroutine scope lives as long as application lives
        use the scope to execute long-running operations throughout app
        default behaviour = if a child fails, other children cancel because the whole scope will cancel
        SupervisorJob = if a child fails, other children keep running (scope still lives)
     */
    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

// explicitly define application coroutine scope
// helpful when having multiple coroutine scopes within the app.
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope