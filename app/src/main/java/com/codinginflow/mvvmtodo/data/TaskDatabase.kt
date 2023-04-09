package com.codinginflow.mvvmtodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codinginflow.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider


@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao()

            //GlobalScope.launch run as long as app is running, (bad practice, no control)

            // defined scope in AppModule.kt
            applicationScope.launch {
                dao.insert(Task("Wash the dishes", completed = true))
                dao.insert(Task("Study for Software Testing", important = true))
                dao.insert(Task("Finish Digital Dossier", important = true))
                dao.insert(Task("Do more research to understand Dependency Injection"))
                dao.insert(Task("Consider Game development c2"))
                dao.insert(Task("Contact group members for cyber-security hub event"))
                dao.insert(Task("Finish Dangerous Dags Big Adventure"))
                dao.insert(Task("study some more"))
            }
        }
    }
}