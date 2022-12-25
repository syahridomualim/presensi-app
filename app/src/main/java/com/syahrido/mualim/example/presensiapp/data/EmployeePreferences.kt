package com.syahrido.mualim.example.presensiapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EmployeePreferences(private val context: Context) {

    suspend fun saveToken(token: String) {
        context.dataStore.edit {
            it[TOKEN] = token
        }
    }

    suspend fun saveIdEmployee(idEmployee: Long) {
        context.dataStore.edit {
            it[ID_EMPLOYEE] = idEmployee
        }
    }

    suspend fun clear() {
        context.dataStore.edit {
            it.clear()
        }
    }

    val token: Flow<String?>
        get() = context.dataStore.data.map {
            it[TOKEN]
        }

    val idEmployee: Flow<Long?>
    get() = context.dataStore.data.map {
        it[ID_EMPLOYEE]
    }



    companion object {
        val ID_EMPLOYEE = longPreferencesKey("ID_EMPLOYEE")
        val TOKEN = stringPreferencesKey("TOKEN")
        val TIME_IN = longPreferencesKey("TIME_IN")
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_datastore")
    }
}