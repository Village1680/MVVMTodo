package com.codinginflow.mvvmtodo.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

// create new entity/table in room SQLite DB
@Entity(tableName = "task_table")

// Parcelize (parcelable) is an interface which lets us send the entire object between fragment instead of individual properties
@Parcelize
// holds data, implements methods which is responsible for comparing two task objects by the data to update view
data class Task(
    // val is immutable (stops changing items which can introduce bugs), make new ones instead
    val name: String,
    val important: Boolean = false,
    val completed: Boolean = false,
    // created date in milliseconds
    val date_in_millis: Long = System.currentTimeMillis(),
    // autogenerate primary key, requires default value so we don't have to pass a value each time
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {
    // dynamically update from date val
    val createdDateFormatted: String
        // override get method
        get() = DateFormat.getDateTimeInstance().format(date_in_millis)


}