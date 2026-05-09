package com.example.hw3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3.data.GamesRepository
import com.example.hw3.data.local.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val cacheTtlHours: Int = 24,
    val historyMaxSize: Int = 50,
    val themeMode: Int = 0
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val gamesRepository: GamesRepository
) : ViewModel() {

    val settingsState: StateFlow<SettingsState> = combine(
        appPreferences.cacheTtlHours,
        appPreferences.historyMaxSize,
        appPreferences.themeMode
    ) { ttl, historySize, theme ->
        SettingsState(ttl, historySize, theme)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SettingsState())

    fun setCacheTtlHours(hours: Int) {
        viewModelScope.launch { appPreferences.setCacheTtlHours(hours) }
    }

    fun setHistoryMaxSize(size: Int) {
        viewModelScope.launch { appPreferences.setHistoryMaxSize(size) }
    }

    fun setThemeMode(mode: Int) {
        viewModelScope.launch { appPreferences.setThemeMode(mode) }
    }

    fun clearCache() {
        viewModelScope.launch { gamesRepository.clearGamesCache() }
    }
}
