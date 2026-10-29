package mobiledev.unb.ca.preferencesdatastoredemo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val PREFERENCES_NAME = "app_preferences"

val Context.appPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name =
    PREFERENCES_NAME
)

// Convert existing SharedPreferences
// Context.appPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
//    name = PREFERENCES_NAME,
//    produceMigrations = { context ->
//        // Automatically reads from "AppPrefs" (old name from SharedPreferencesDemo)
//        // and copies keys into DataStore
//        listOf(SharedPreferencesMigration(context, "AppPrefs"))
//    }
//)