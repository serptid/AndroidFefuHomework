package com.example.hw3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3.data.GamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val repository: GamesRepository
) : ViewModel() {

    private val _gameDetailState = MutableStateFlow<GameDetailState>(UiState.Loading)
    val gameDetailState: StateFlow<GameDetailState> = _gameDetailState.asStateFlow()

    private var lastDetailId: Int? = null

    fun loadGameDetail(id: Int) {
        lastDetailId = id
        _gameDetailState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val game = repository.getGameDetail(id)
                _gameDetailState.value = UiState.Success(game)
            } catch (e: Exception) {
                _gameDetailState.value = UiState.Error(friendlyError(e))
            }
        }
    }

    fun retryDetail() {
        val id = lastDetailId ?: return
        loadGameDetail(id)
    }
}
