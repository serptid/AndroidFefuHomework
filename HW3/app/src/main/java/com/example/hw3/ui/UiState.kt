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

fun friendlyError(e: Exception): String = when (e) {
    is java.net.SocketTimeoutException -> "Превышено время ожидания. Попробуйте другую сеть или VPN."
    is java.net.UnknownHostException -> "Нет подключения к интернету."
    is java.io.IOException -> "Ошибка сети."
    else -> e.message ?: "Неизвестная ошибка"
}
