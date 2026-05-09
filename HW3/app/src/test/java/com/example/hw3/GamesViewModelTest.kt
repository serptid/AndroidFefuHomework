package com.example.hw3

import app.cash.turbine.test
import com.example.hw3.data.Game
import com.example.hw3.data.GameDetail
import com.example.hw3.data.local.AppPreferences
import com.example.hw3.ui.GameDetailViewModel
import com.example.hw3.ui.GamesListViewModel
import com.example.hw3.ui.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GamesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeGamesRepository
    private lateinit var historyRepository: FakeHistoryRepository
    private lateinit var statusRepository: FakeStatusRepository
    private lateinit var appPreferences: AppPreferences
    private lateinit var gamesListVM: GamesListViewModel
    private lateinit var gameDetailVM: GameDetailViewModel

    private val testGame = Game(
        id = 1,
        title = "Test Game",
        thumbnail = "https://example.com/thumb.jpg",
        shortDescription = "A test game",
        genre = "Action",
        platform = "PC",
        releaseDate = "2023-01-01"
    )

    private val testGameDetail = GameDetail(
        id = 1,
        title = "Test Game",
        thumbnail = "https://example.com/thumb.jpg",
        description = "Full description of the game",
        genre = "Action",
        platform = "PC",
        releaseDate = "2023-01-01",
        publisher = "Test Publisher",
        developer = "Test Developer",
        gameUrl = "https://example.com/game"
    )

    @Before
    fun setup() {
        repository = FakeGamesRepository()
        historyRepository = FakeHistoryRepository()
        statusRepository = FakeStatusRepository()
        appPreferences = AppPreferences(FakePreferencesDataStore())
        gamesListVM = GamesListViewModel(repository, statusRepository, appPreferences)
        gameDetailVM = GameDetailViewModel(repository, historyRepository, statusRepository, appPreferences)
    }

    @Test
    fun initialGamesState_isLoading() {
        assertEquals(UiState.Loading, gamesListVM.uiState.value.games)
    }

    @Test
    fun loadGames_success_setsSuccessState() = runTest(mainDispatcherRule.testDispatcher) {
        repository.gamesResult = Result.success(listOf(testGame))

        gamesListVM.loadGames()
        advanceUntilIdle()

        assertEquals(UiState.Success(listOf(testGame)), gamesListVM.uiState.value.games)
    }

    @Test
    fun loadGames_error_setsErrorState() = runTest(mainDispatcherRule.testDispatcher) {
        repository.gamesResult = Result.failure(java.io.IOException())

        gamesListVM.loadGames()
        advanceUntilIdle()

        assertTrue(gamesListVM.uiState.value.games is UiState.Error)
        assertEquals("Ошибка сети.", (gamesListVM.uiState.value.games as UiState.Error).message)
    }

    @Test
    fun onQueryChange_noMatch_emitsEmptyState() = runTest(mainDispatcherRule.testDispatcher) {
        repository.gamesResult = Result.success(listOf(testGame))
        gamesListVM.loadGames()
        advanceUntilIdle()

        gamesListVM.uiState.test {
            awaitItem()
            gamesListVM.onQueryChange("xyznonexistent_query")
            advanceTimeBy(400)
            val next = awaitItem()
            assertTrue(next.games is UiState.Empty)
            assertFalse(next.games is UiState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun retryDetail_emitsErrorThenLoadingThenSuccess() = runTest(mainDispatcherRule.testDispatcher) {
        repository.gameDetailResult = Result.failure(java.io.IOException())
        gameDetailVM.loadGameDetail(testGame.id)
        advanceUntilIdle()

        repository.gameDetailResult = Result.success(testGameDetail)

        gameDetailVM.gameDetailState.test {
            assertTrue(awaitItem() is UiState.Error)

            gameDetailVM.retryDetail()

            assertTrue(awaitItem() is UiState.Loading)
            val success = awaitItem()
            assertTrue(success is UiState.Success)
            assertEquals(testGameDetail, (success as UiState.Success).data)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun toggleFavourite_whenAlreadyFavourite_removes() = runTest(mainDispatcherRule.testDispatcher) {
        repository.favouritesFlow.value = listOf(testGame)
        advanceUntilIdle()

        gamesListVM.toggleFavourite(testGame)
        advanceUntilIdle()

        assertEquals(1, repository.removeFavouriteCallCount)
        assertEquals(0, repository.addFavouriteCallCount)
    }

    @Test
    fun refreshGames_reloadsEvenWhenAlreadySuccess() = runTest(mainDispatcherRule.testDispatcher) {
        repository.gamesResult = Result.success(listOf(testGame))
        gamesListVM.loadGames()
        advanceUntilIdle()
        assertEquals(UiState.Success(listOf(testGame)), gamesListVM.uiState.value.games)

        val updatedGame = testGame.copy(title = "Updated Game")
        repository.gamesResult = Result.success(listOf(updatedGame))
        gamesListVM.refreshGames()
        advanceUntilIdle()

        assertEquals(UiState.Success(listOf(updatedGame)), gamesListVM.uiState.value.games)
        assertFalse(gamesListVM.uiState.value.isRefreshing)
    }

    @Test
    fun favouritesFlow_sequence_emptyThenGame() = runTest(mainDispatcherRule.testDispatcher) {
        repository.getFavouriteGames().test {
            assertEquals(emptyList<Game>(), awaitItem())

            repository.favouritesFlow.emit(listOf(testGame))
            assertEquals(listOf(testGame), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun favouritesFlow_noExtraEmissions_onSameState() = runTest(mainDispatcherRule.testDispatcher) {
        val games = listOf(testGame)
        repository.favouritesFlow.emit(games)

        repository.getFavouriteGames().test {
            assertEquals(games, awaitItem())

            repository.favouritesFlow.emit(games)
            expectNoEvents()

            cancel()
        }
    }
}
