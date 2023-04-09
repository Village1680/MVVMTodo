package com.codinginflow.mvvmtodo.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


// abstraction layer for jetpack datastore code

private const val TAG = "PreferencesManager"
// two distinct sorting states represented by single values
enum class SortOrder {BY_NAME, BY_DATE}

// wrapper class for current preferences to return both values from map function
data class FilterPreferences(val sortOrder: SortOrder, val hideCompleted: Boolean)


@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context){

    private val dataStore = context.createDataStore("user_preferences")

    // transform user preferences flow
    val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences:", exception)
                // if IOException, instead of crash use default preferences
                emit(emptyPreferences())
            } else { //rethrow any other type of exception
                throw exception
            }
        }
        .map { preferences ->
            // transform enum to primative type (String)
            val sortOrder = SortOrder.valueOf(
                // read current value from dataStore.data (?: elvis operator to use default value in case of null preference)
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name
            )
            val hideCompleted = preferences[PreferencesKeys.HIDE_COMPLETED] ?: false
            // return values
            FilterPreferences(sortOrder, hideCompleted)
        }

    // update existing preferences asynchronously
    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideCompleted(hideCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HIDE_COMPLETED] = hideCompleted
        }
    }

    // don't need object, but better for organisation
    private object PreferencesKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}