package com.example.hw3.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3.data.Game
import com.example.hw3.data.GamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val repository: GamesRepository
) : ViewModel() {

    var favouritesState: FavouritesState by mutableStateOf(UiState.Loading)
        private set

    init {
        observeFavourites()
    }

    private fun observeFavourites() {
        viewModelScope.launch {
            repository.getFavouriteGames().collect { favourites ->
                favouritesState = if (favourites.isEmpty()) UiState.Empty
                else UiState.Success(favourites)
            }
        }
    }

    fun removeFavourite(game: Game) {
        viewModelScope.launch {
            repository.removeFavourite(game.id)
        }
    }
}
