package com.example.hw3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3.data.GamesRepository
import com.example.hw3.data.HistoryRepository
import com.example.hw3.data.StatusRepository
import com.example.hw3.data.local.AppPreferences
import com.example.hw3.data.local.UserGameStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val repository: GamesRepository,
    private val historyRepository: HistoryRepository,
    private val statusRepository: StatusRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _gameDetailState = MutableStateFlow<GameDetailState>(UiState.Loading)
    val gameDetailState: StateFlow<GameDetailState> = _gameDetailState.asStateFlow()

    private val _currentGameId = MutableStateFlow<Int?>(null)

    val gameStatus: StateFlow<UserGameStatus?> = _currentGameId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else statusRepository.observeStatus(id)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun loadGameDetail(id: Int) {
        _currentGameId.value = id
        _gameDetailState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val game = repository.getGameDetail(id)
                _gameDetailState.value = UiState.Success(game)
                val maxSize = appPreferences.historyMaxSize.first()
                historyRepository.addToHistory(
                    gameId = game.id,
                    title = game.title,
                    thumbnail = game.thumbnail,
                    genre = game.genre,
                    platform = game.platform,
                    maxSize = maxSize
                )
            } catch (e: Exception) {
                _gameDetailState.value = UiState.Error(friendlyError(e))
            }
        }
    }

    fun retryDetail() {
        val id = _currentGameId.value ?: return
        loadGameDetail(id)
    }

    fun setStatus(status: UserGameStatus) {
        val id = _currentGameId.value ?: return
        val title = (_gameDetailState.value as? UiState.Success)?.data?.title ?: ""
        viewModelScope.launch { statusRepository.setStatus(id, title, status) }
    }

    fun clearStatus() {
        val id = _currentGameId.value ?: return
        viewModelScope.launch { statusRepository.clearStatus(id) }
    }
}
