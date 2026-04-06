package com.example.hw3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3.data.Game
import com.example.hw3.data.GamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GamesScreenState(
    val games: GamesListState = UiState.Loading,
    val isRefreshing: Boolean = false,
    val favouriteIds: Set<Int> = emptySet()
)

@HiltViewModel
class GamesListViewModel @Inject constructor(
    private val repository: GamesRepository
) : ViewModel() {

    private val searchFlow = MutableStateFlow("")
    private val loadTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val _isRefreshing = MutableStateFlow(false)

    val searchQuery: StateFlow<String> = searchFlow.asStateFlow()

    private val rawGamesFlow: StateFlow<GamesListState> = loadTrigger
        .flatMapLatest {
            flow<GamesListState> {
                emit(UiState.Loading)
                try {
                    emit(UiState.Success(repository.getGames()))
                } catch (e: Exception) {
                    emit(UiState.Error(friendlyError(e)))
                }
            }
        }
        .onEach { if (it !is UiState.Loading) _isRefreshing.value = false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UiState.Loading
        )

    private val favouriteIdsFlow: StateFlow<Set<Int>> = repository.getFavouriteGames()
        .map { games -> games.map { it.id }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptySet()
        )

    val uiState: StateFlow<GamesScreenState> = combine(
        searchFlow.debounce(350).distinctUntilChanged(),
        rawGamesFlow,
        favouriteIdsFlow,
        _isRefreshing
    ) { q, apiState, favIds, refreshing ->
        val gamesState: GamesListState = when (apiState) {
            is UiState.Loading -> UiState.Loading
            is UiState.Error -> apiState
            is UiState.Success -> {
                val filtered = if (q.isBlank()) apiState.data
                else apiState.data.filter { it.title.contains(q, ignoreCase = true) }
                if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered)
            }
            is UiState.Empty -> UiState.Empty
        }
        GamesScreenState(gamesState, refreshing, favIds)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GamesScreenState()
    )

    fun loadGames() {
        if (rawGamesFlow.value is UiState.Success) return
        loadTrigger.tryEmit(Unit)
    }

    fun refreshGames() {
        _isRefreshing.value = true
        loadTrigger.tryEmit(Unit)
    }

    fun onQueryChange(newQuery: String) {
        searchFlow.value = newQuery
    }

    fun toggleFavourite(game: Game) {
        viewModelScope.launch {
            if (favouriteIdsFlow.value.contains(game.id)) {
                repository.removeFavourite(game.id)
            } else {
                repository.addFavourite(game)
            }
        }
    }
}
