package mobiledev.unb.ca.preferencesdatastoredemo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreRepository(private val dataStore: DataStore<Preferences>) {
    fun getIntFlow(keyName: String, defaultValue: Int = 0): Flow<Int> {
        val key = intPreferencesKey(keyName)
        return dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    suspend fun saveIntValue(keyName: String, value: Int) {
        val key = intPreferencesKey(keyName)
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun removeIntValue(keyName: String) {
        val key = intPreferencesKey(keyName)
        dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }
}
