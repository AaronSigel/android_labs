package com.example.labs_app.storage

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_datastore")

/** Дефолтные значения настроек (DataStore). */
const val DEFAULT_USER_EMAIL = "test_user@example.com"
const val DEFAULT_NOTIFICATIONS_ENABLED = true
const val DEFAULT_DARK_THEME = false

/**
 * Менеджер настроек в DataStore: email/никнейм, уведомления, тёмная тема.
 */
class PreferencesDataStoreManager(private val context: Context) {

    private val logTag: String get() = "LabsApp/${javaClass.simpleName}"

    private val dataStore = context.dataStore

    private object Keys {
        val userEmail = stringPreferencesKey("user_email")
        val notificationsEnabled = booleanPreferencesKey("notifications_enabled")
        val darkTheme = booleanPreferencesKey("dark_theme")
    }

    val userEmailFlow: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.userEmail] ?: DEFAULT_USER_EMAIL
    }

    val notificationsEnabledFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.notificationsEnabled] ?: DEFAULT_NOTIFICATIONS_ENABLED
    }

    val darkThemeFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.darkTheme] ?: DEFAULT_DARK_THEME
    }

    suspend fun setUserEmail(value: String) {
        dataStore.edit { it[Keys.userEmail] = value }
    }

    suspend fun setNotificationsEnabled(value: Boolean) {
        dataStore.edit { it[Keys.notificationsEnabled] = value }
    }

    suspend fun setDarkTheme(value: Boolean) {
        dataStore.edit { it[Keys.darkTheme] = value }
    }

    suspend fun setAll(
        userEmail: String,
        notificationsEnabled: Boolean,
        darkTheme: Boolean
    ) {
        Log.d(logTag, "setAll: userEmail=$userEmail, notificationsEnabled=$notificationsEnabled, darkTheme=$darkTheme")
        dataStore.edit { prefs ->
            prefs[Keys.userEmail] = userEmail
            prefs[Keys.notificationsEnabled] = notificationsEnabled
            prefs[Keys.darkTheme] = darkTheme
        }
    }

    suspend fun getUserEmail(): String = dataStore.data.map { it[Keys.userEmail] ?: DEFAULT_USER_EMAIL }.first()
    suspend fun getNotificationsEnabled(): Boolean = dataStore.data.map { it[Keys.notificationsEnabled] ?: DEFAULT_NOTIFICATIONS_ENABLED }.first()
    suspend fun getDarkTheme(): Boolean = dataStore.data.map { it[Keys.darkTheme] ?: DEFAULT_DARK_THEME }.first()
}
