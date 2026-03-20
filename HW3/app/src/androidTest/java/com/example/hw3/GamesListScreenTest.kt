package com.example.hw3

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hw3.data.Game
import com.example.hw3.ui.UiState
import com.example.hw3.ui.screens.GamesListScreen
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GamesListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testGame = Game(
        id = 1,
        title = "Warframe",
        thumbnail = "https://example.com/warframe.jpg",
        shortDescription = "A free-to-play action game",
        genre = "Action",
        platform = "PC",
        releaseDate = "2013-03-25"
    )

    @Test
    fun loadingState_showsProgressIndicator() {
        composeTestRule.setContent {
            GamesListScreen(
                state = UiState.Loading,
                query = "",
                isRefreshing = false,
                onQueryChange = {},
                onFirstLoad = {},
                onRefresh = {},
                onRetry = {},
                onGameClick = {},
                onFavouritesClick = {},
                onToggleFavourite = {},
                isFavourite = { false }
            )
        }

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertExists()
    }

    @Test
    fun successState_showsGameTitles() {
        composeTestRule.setContent {
            GamesListScreen(
                state = UiState.Success(listOf(testGame)),
                query = "",
                isRefreshing = false,
                onQueryChange = {},
                onFirstLoad = {},
                onRefresh = {},
                onRetry = {},
                onGameClick = {},
                onFavouritesClick = {},
                onToggleFavourite = {},
                isFavourite = { false }
            )
        }

        composeTestRule.onNodeWithText(testGame.title).assertIsDisplayed()
    }

    @Test
    fun errorState_retryButton_invokesCallback() {
        var retryCalled = false

        composeTestRule.setContent {
            GamesListScreen(
                state = UiState.Error("Ошибка сети."),
                query = "",
                isRefreshing = false,
                onQueryChange = {},
                onFirstLoad = {},
                onRefresh = {},
                onRetry = { retryCalled = true },
                onGameClick = {},
                onFavouritesClick = {},
                onToggleFavourite = {},
                isFavourite = { false }
            )
        }

        composeTestRule.onNodeWithText("Retry").performClick()
        assertTrue(retryCalled)
    }
}
