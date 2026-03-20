package com.example.hw3

import app.cash.turbine.test
import com.example.hw3.data.Game
import com.example.hw3.data.GameDetail
import com.example.hw3.ui.GameDetailViewModel
import com.example.hw3.ui.GamesListViewModel
import com.example.hw3.ui.UiState
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GamesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeGamesRepository
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
        gamesListVM = GamesListViewModel(repository)
        gameDetailVM = GameDetailViewModel(repository)
    }

    @Test
    fun initialGamesState_isLoading() {
        assertEquals(UiState.Loading, gamesListVM.gamesState)
    }

    @Test
    fun loadGames_success_setsSuccessState() = runTest(mainDispatcherRule.testDispatcher) {
        repository.gamesResult = Result.success(listOf(testGame))

        gamesListVM.loadGames()
        advanceUntilIdle()

        assertEquals(UiState.Success(listOf(testGame)), gamesListVM.gamesState)
    }

    @Test
    fun loadGames_error_setsErrorState() = runTest(mainDispatcherRule.testDispatcher) {
        repository.gamesResult = Result.failure(java.io.IOException())

        gamesListVM.loadGames()
        advanceUntilIdle()

        assertTrue(gamesListVM.gamesState is UiState.Error)
        assertEquals("Ошибка сети.", (gamesListVM.gamesState as UiState.Error).message)
    }

    @Test
    fun onQueryChange_noMatch_setsEmptyState() = runTest(mainDispatcherRule.testDispatcher) {
        repository.gamesResult = Result.success(listOf(testGame))
        gamesListVM.loadGames()
        advanceUntilIdle()

        gamesListVM.onQueryChange("xyznonexistent_query")
        advanceTimeBy(400)

        assertTrue(gamesListVM.gamesState is UiState.Empty)
        assertFalse(gamesListVM.gamesState is UiState.Success)
    }

    @Test
    fun retryDetail_callsRepositoryAgain() = runTest(mainDispatcherRule.testDispatcher) {
        repository.gameDetailResult = Result.success(testGameDetail)

        gameDetailVM.loadGameDetail(testGame.id)
        advanceUntilIdle()
        assertEquals(1, repository.getDetailCallCount)

        gameDetailVM.retryDetail()
        advanceUntilIdle()
        assertEquals(2, repository.getDetailCallCount)
    }

    @Test
    fun toggleFavourite_whenAlreadyFavourite_removes() = runTest(mainDispatcherRule.testDispatcher) {
        repository.isFavouriteResult = true

        gamesListVM.toggleFavourite(testGame)
        advanceUntilIdle()

        assertEquals(1, repository.removeFavouriteCallCount)
        assertEquals(0, repository.addFavouriteCallCount)
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
