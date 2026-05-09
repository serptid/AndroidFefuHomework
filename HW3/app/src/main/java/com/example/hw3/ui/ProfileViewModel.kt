package com.example.hw3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3.data.GamesRepository
import com.example.hw3.data.HistoryRepository
import com.example.hw3.data.StatusRepository
import com.example.hw3.data.local.AppPreferences
import com.example.hw3.data.local.UserGameStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val username: String = "",
    val viewedCount: Int = 0,
    val favouritesCount: Int = 0,
    val topGenre: String = "",
    val statusCounts: Map<UserGameStatus, Int> = emptyMap()
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val historyRepository: HistoryRepository,
    private val gamesRepository: GamesRepository,
    private val statusRepository: StatusRepository
) : ViewModel() {

    val profileState: StateFlow<ProfileState> = combine(
        appPreferences.username,
        historyRepository.observeHistory(),
        gamesRepository.getFavouriteGames(),
        statusRepository.observeAllStatuses()
    ) { username, history, favourites, statuses ->
        val topGenre = history
            .groupBy { it.genre }
            .maxByOrNull { it.value.size }
            ?.key ?: ""
        val statusCounts = statuses.values
            .groupBy { it }
            .mapValues { (_, list) -> list.size }
        ProfileState(
            username = username,
            viewedCount = history.size,
            favouritesCount = favourites.size,
            topGenre = topGenre,
            statusCounts = statusCounts
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ProfileState())

    fun setUsername(name: String) {
        viewModelScope.launch { appPreferences.setUsername(name) }
    }
}
