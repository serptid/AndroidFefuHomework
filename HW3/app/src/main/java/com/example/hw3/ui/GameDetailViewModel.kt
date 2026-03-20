package com.example.hw3.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3.data.GamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val repository: GamesRepository
) : ViewModel() {

    var gameDetailState: GameDetailState by mutableStateOf(UiState.Loading)
        private set

    private var lastDetailId: Int? = null

    fun loadGameDetail(id: Int) {
        lastDetailId = id
        gameDetailState = UiState.Loading
        viewModelScope.launch {
            try {
                val game = repository.getGameDetail(id)
                gameDetailState = UiState.Success(game)
            } catch (e: Exception) {
                gameDetailState = UiState.Error(friendlyError(e))
            }
        }
    }

    fun retryDetail() {
        val id = lastDetailId ?: return
        loadGameDetail(id)
    }
}
