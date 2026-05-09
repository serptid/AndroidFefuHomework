package com.example.hw3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3.data.Game
import com.example.hw3.data.GamesRepository
import com.example.hw3.data.StatusRepository
import com.example.hw3.data.local.AppPreferences
import com.example.hw3.data.local.UserGameStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOrder { BY_TITLE, BY_DATE, BY_GENRE }

data class GamesScreenState(
    val games: GamesListState = UiState.Loading,
    val isRefreshing: Boolean = false,
    val favouriteIds: Set<Int> = emptySet(),
    val statusMap: Map<Int, UserGameStatus> = emptyMap(),
    val genres: List<String> = emptyList(),
    val selectedGenre: String? = null,
    val sortOrder: SortOrder = SortOrder.BY_TITLE
)

@HiltViewModel
class GamesListViewModel @Inject constructor(
    private val repository: GamesRepository,
    private val statusRepository: StatusRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val searchFlow = MutableStateFlow("")
    private val loadTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val _isRefreshing = MutableStateFlow(false)
    private val _selectedGenre = MutableStateFlow<String?>(null)
    private val _sortOrder: StateFlow<SortOrder> = appPreferences.sortOrderIndex
        .map { index -> SortOrder.entries.getOrElse(index) { SortOrder.BY_TITLE } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SortOrder.BY_TITLE)

    val searchQuery: StateFlow<String> = searchFlow.asStateFlow()

    private val rawGamesFlow: StateFlow<GamesListState> = loadTrigger
        .flatMapLatest {
            flow<GamesListState> {
                emit(UiState.Loading)
                try {
                    val ttl = appPreferences.cacheTtlHours.first()
                    emit(UiState.Success(repository.getGames(ttl)))
                } catch (e: Exception) {
                    emit(UiState.Error(friendlyError(e)))
                }
            }
        }
        .onEach { state ->
            if (state !is UiState.Loading) _isRefreshing.value = false
            if (state is UiState.Success) {
                viewModelScope.launch { prefetchAllDetails(state.data) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UiState.Loading
        )

    private val favouriteIdsFlow: StateFlow<Set<Int>> = repository.getFavouriteGames()
        .map { games -> games.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    private val statusMapFlow: StateFlow<Map<Int, UserGameStatus>> =
        statusRepository.observeAllStatuses()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val uiState: StateFlow<GamesScreenState> = combine(
        searchFlow.debounce(350).distinctUntilChanged(),
        rawGamesFlow,
        favouriteIdsFlow,
        _isRefreshing,
        statusMapFlow
    ) { q, apiState, favIds, refreshing, statuses ->
        val allGames = if (apiState is UiState.Success) apiState.data else emptyList()
        val genres = allGames.map { it.genre }.distinct().sorted()
        val textFiltered = if (q.isBlank()) allGames
        else allGames.filter { it.title.contains(q, ignoreCase = true) }
        val gamesState: GamesListState = when {
            apiState is UiState.Loading -> UiState.Loading
            apiState is UiState.Error -> apiState
            textFiltered.isEmpty() -> UiState.Empty
            else -> UiState.Success(textFiltered)
        }
        GamesScreenState(gamesState, refreshing, favIds, statuses, genres, null, SortOrder.BY_TITLE)
    }.combine(_selectedGenre) { base, genre ->
        val genreFiltered: GamesListState = when (val gs = base.games) {
            is UiState.Success -> {
                val list = if (genre == null) gs.data else gs.data.filter { it.genre == genre }
                if (list.isEmpty()) UiState.Empty else UiState.Success(list)
            }
            else -> gs
        }
        base.copy(games = genreFiltered, selectedGenre = genre)
    }.combine(_sortOrder) { base, sort ->
        val sortedGames: GamesListState = when (val gs = base.games) {
            is UiState.Success -> UiState.Success(
                when (sort) {
                    SortOrder.BY_TITLE -> gs.data.sortedBy { it.title }
                    SortOrder.BY_DATE -> gs.data.sortedByDescending { it.releaseDate }
                    SortOrder.BY_GENRE -> gs.data.sortedBy { it.genre }
                }
            )
            else -> gs
        }
        base.copy(games = sortedGames, sortOrder = sort)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GamesScreenState()
    )

    private suspend fun prefetchAllDetails(games: List<Game>) {
        games.forEach { game ->
            runCatching { repository.getGameDetail(game.id) }
        }
    }

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

    fun selectGenre(genre: String?) {
        _selectedGenre.value = genre
    }

    fun setSortOrder(order: SortOrder) {
        viewModelScope.launch { appPreferences.setSortOrderIndex(order.ordinal) }
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
