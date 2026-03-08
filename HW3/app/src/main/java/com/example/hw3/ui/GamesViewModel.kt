package com.example.hw3.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3.data.Game
import com.example.hw3.data.GamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamesViewModel @Inject constructor(
    private val repository: GamesRepository
) : ViewModel() {

    private var allGames: List<Game> = emptyList()

    var query: String by mutableStateOf("")
        private set

    var gamesState: GamesListState by mutableStateOf(UiState.Loading)
        private set

    var isRefreshing: Boolean by mutableStateOf(false)
        private set

    private var searchJob: Job? = null

    private var favouriteIds: Set<Int> by mutableStateOf(emptySet())

    var favouritesState: FavouritesState by mutableStateOf(UiState.Empty)
        private set

    var gameDetailState: GameDetailState by mutableStateOf(UiState.Loading)
        private set

    private var lastDetailId: Int? = null

    init {
        observeFavourites()
    }

    private fun observeFavourites() {
        viewModelScope.launch {
            repository.getFavouriteGames().collectLatest { favourites ->
                favouriteIds = favourites.map { it.id }.toSet()
                favouritesState =
                    if (favourites.isEmpty()) UiState.Empty
                    else UiState.Success(favourites)
            }
        }
    }

    fun isFavourite(id: Int): Boolean = favouriteIds.contains(id)

    fun toggleFavourite(game: Game) {
        viewModelScope.launch {
            if (repository.isFavourite(game.id)) {
                repository.removeFavourite(game.id)
            } else {
                repository.addFavourite(game)
            }
        }
    }

    fun loadGames() {
        if (allGames.isNotEmpty()) {
            applyFilterNow()
            return
        }

        gamesState = UiState.Loading
        viewModelScope.launch {
            try {
                allGames = repository.getGames()
                applyFilterNow()
            } catch (e: Exception) {
                gamesState = UiState.Error(friendlyError(e))
            }
        }
    }

    fun refreshGames() {
        viewModelScope.launch {
            isRefreshing = true
            try {
                allGames = repository.getGames()
                applyFilterNow()
            } catch (e: Exception) {
                gamesState = UiState.Error(friendlyError(e))
            } finally {
                isRefreshing = false
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        query = newQuery
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(350)
            applyFilterNow()
        }
    }

    private fun applyFilterNow() {
        val q = query.trim().lowercase()
        val filtered = if (q.isEmpty()) allGames else allGames.filter {
            it.title.lowercase().contains(q)
        }

        gamesState =
            if (filtered.isEmpty()) UiState.Empty
            else UiState.Success(filtered)
    }

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

    private fun friendlyError(e: Exception): String =
        when (e) {
            is java.net.SocketTimeoutException ->
                "Превышено время ожидания. Попробуйте другую сеть или VPN."
            is java.net.UnknownHostException ->
                "Нет подключения к интернету."
            is java.io.IOException ->
                "Ошибка сети."
            else -> e.message ?: "Неизвестная ошибка"
        }
}
