package com.example.hw3.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3.data.Game
import com.example.hw3.data.GamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamesListViewModel @Inject constructor(
    private val repository: GamesRepository
) : ViewModel() {

    // ── UI-facing state (mutableStateOf → Compose recomposition) ───────────
    var query: String by mutableStateOf("")
        private set
    var gamesState: GamesListState by mutableStateOf(UiState.Loading)
        private set
    var isRefreshing: Boolean by mutableStateOf(false)
        private set
    private var favouriteIds: Set<Int> by mutableStateOf(emptySet())

    // ── Source 1: search query ──────────────────────────────────────────────
    private val searchFlow = MutableStateFlow("")

    // ── Source 2: load / refresh actions → API data ────────────────────────
    //    SharedFlow(replay=1): поздний подписчик получит последний триггер
    //    (важно при StandardTestDispatcher, когда подписка стартует позже emit-а)
    private val loadTrigger = MutableSharedFlow<Unit>(replay = 1)

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
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UiState.Loading
        )

    // ── Source 3: favourite IDs из Room (data layer, реактивный) ───────────
    private val favouriteIdsFlow: StateFlow<Set<Int>> = repository.getFavouriteGames()
        .map { games -> games.map { it.id }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptySet()
        )

    // ── Реактивный пайплайн: combine(3 источника) → состояние экрана ───────
    //    операторы: combine, flatMapLatest, debounce, distinctUntilChanged, map
    init {
        viewModelScope.launch {
            combine(
                searchFlow.debounce(350).distinctUntilChanged(),
                rawGamesFlow,
                favouriteIdsFlow
            ) { q, apiState, favIds ->
                val newState: GamesListState = when (apiState) {
                    is UiState.Loading -> UiState.Loading
                    is UiState.Error   -> apiState
                    is UiState.Success -> {
                        val filtered = if (q.isBlank()) apiState.data
                        else apiState.data.filter { it.title.contains(q, ignoreCase = true) }
                        if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered)
                    }
                    is UiState.Empty   -> UiState.Empty
                }
                Pair(newState, favIds)
            }.collect { (newState, newFavIds) ->
                gamesState = newState
                favouriteIds = newFavIds
                if (newState !is UiState.Loading) isRefreshing = false
            }
        }
    }

    // ── Public API (контракт не изменился → AppNavGraph и тесты без правок) ─

    /** Первичная загрузка: если данные уже есть — ничего не делает. */
    fun loadGames() {
        if (rawGamesFlow.value is UiState.Success) return
        loadTrigger.tryEmit(Unit)
    }

    /** Pull-to-refresh: всегда перезапрашивает данные. */
    fun refreshGames() {
        isRefreshing = true
        loadTrigger.tryEmit(Unit)
    }

    fun onQueryChange(newQuery: String) {
        query = newQuery              // сразу обновляет TextField (без задержки)
        searchFlow.value = newQuery  // входит в реактивный пайплайн (с debounce)
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
}
