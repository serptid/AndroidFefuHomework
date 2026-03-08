package com.example.hw3.ui

import com.example.hw3.data.Game
import com.example.hw3.data.GameDetail

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Error(val message: String) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
}


typealias GamesListState = UiState<List<Game>>
typealias GameDetailState = UiState<GameDetail>
typealias FavouritesState = UiState<List<Game>>
