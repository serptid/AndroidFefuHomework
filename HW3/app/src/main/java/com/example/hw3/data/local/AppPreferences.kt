package com.example.hw3.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val CACHE_TTL_HOURS = intPreferencesKey("cache_ttl_hours")
        val HISTORY_MAX_SIZE = intPreferencesKey("history_max_size")
        val THEME_MODE = intPreferencesKey("theme_mode")
        val USERNAME = stringPreferencesKey("username")
        val SORT_ORDER = intPreferencesKey("sort_order")
    }

    val cacheTtlHours: Flow<Int> = dataStore.data.map { it[Keys.CACHE_TTL_HOURS] ?: 24 }
    val historyMaxSize: Flow<Int> = dataStore.data.map { it[Keys.HISTORY_MAX_SIZE] ?: 50 }
    val themeMode: Flow<Int> = dataStore.data.map { it[Keys.THEME_MODE] ?: 0 }
    val username: Flow<String> = dataStore.data.map { it[Keys.USERNAME] ?: "" }
    val sortOrderIndex: Flow<Int> = dataStore.data.map { it[Keys.SORT_ORDER] ?: 0 }

    suspend fun setCacheTtlHours(hours: Int) {
        dataStore.edit { it[Keys.CACHE_TTL_HOURS] = hours }
    }

    suspend fun setHistoryMaxSize(size: Int) {
        dataStore.edit { it[Keys.HISTORY_MAX_SIZE] = size }
    }

    suspend fun setThemeMode(mode: Int) {
        dataStore.edit { it[Keys.THEME_MODE] = mode }
    }

    suspend fun setUsername(name: String) {
        dataStore.edit { it[Keys.USERNAME] = name }
    }

    suspend fun setSortOrderIndex(index: Int) {
        dataStore.edit { it[Keys.SORT_ORDER] = index }
    }
}
