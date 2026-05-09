package com.example.hw3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3.data.Game
import com.example.hw3.data.GamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val repository: GamesRepository
) : ViewModel() {

    val favouritesState: StateFlow<FavouritesState> = repository.getFavouriteGames()
        .map { games -> if (games.isEmpty()) UiState.Empty else UiState.Success(games) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    fun removeFavourite(game: Game) {
        viewModelScope.launch {
            repository.removeFavourite(game.id)
        }
    }
}
