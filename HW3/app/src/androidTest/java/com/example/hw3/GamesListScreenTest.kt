package com.example.hw3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hw3.data.Game
import com.example.hw3.ui.Routes
import com.example.hw3.ui.UiState
import com.example.hw3.ui.screens.GamesListScreen
import org.junit.Assert.assertEquals
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
    fun errorRetry_transitionsToSuccess_showsGameTitle() {
        var state: UiState<List<Game>> by mutableStateOf(UiState.Error("Ошибка сети."))

        composeTestRule.setContent {
            GamesListScreen(
                state = state,
                query = "",
                isRefreshing = false,
                onQueryChange = {},
                onFirstLoad = {},
                onRefresh = {},
                onRetry = { state = UiState.Success(listOf(testGame)) },
                onGameClick = {},
                onFavouritesClick = {},
                onToggleFavourite = {},
                isFavourite = { false }
            )
        }

        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").performClick()
        composeTestRule.onNodeWithText(testGame.title).assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertDoesNotExist()
    }

    @Test
    fun clickGame_navigatesToGameDetail_withCorrectId() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        composeTestRule.setContent {
            navController.navigatorProvider.addNavigator(
                androidx.navigation.compose.ComposeNavigator()
            )
            NavHost(navController = navController, startDestination = Routes.GAMES_LIST) {
                composable(Routes.GAMES_LIST) {
                    GamesListScreen(
                        state = UiState.Success(listOf(testGame)),
                        query = "",
                        isRefreshing = false,
                        onQueryChange = {},
                        onFirstLoad = {},
                        onRefresh = {},
                        onRetry = {},
                        onGameClick = { id -> navController.navigate("${Routes.GAME_DETAIL}/$id") },
                        onFavouritesClick = { navController.navigate(Routes.FAVOURITES) },
                        onToggleFavourite = {},
                        isFavourite = { false }
                    )
                }
                composable(
                    route = "${Routes.GAME_DETAIL}/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { androidx.compose.material3.Text("Detail") }
                composable(Routes.FAVOURITES) { androidx.compose.material3.Text("Favourites") }
            }
        }

        composeTestRule.onNodeWithText(testGame.title).performClick()

        assertEquals("${Routes.GAME_DETAIL}/{id}", navController.currentDestination?.route)
        assertEquals(testGame.id, navController.currentBackStackEntry?.arguments?.getInt("id"))
    }

    @Test
    fun clickFavouritesIcon_navigatesToFavouritesScreen() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        composeTestRule.setContent {
            navController.navigatorProvider.addNavigator(
                androidx.navigation.compose.ComposeNavigator()
            )
            NavHost(navController = navController, startDestination = Routes.GAMES_LIST) {
                composable(Routes.GAMES_LIST) {
                    GamesListScreen(
                        state = UiState.Success(listOf(testGame)),
                        query = "",
                        isRefreshing = false,
                        onQueryChange = {},
                        onFirstLoad = {},
                        onRefresh = {},
                        onRetry = {},
                        onGameClick = { id -> navController.navigate("${Routes.GAME_DETAIL}/$id") },
                        onFavouritesClick = { navController.navigate(Routes.FAVOURITES) },
                        onToggleFavourite = {},
                        isFavourite = { false }
                    )
                }
                composable(
                    route = "${Routes.GAME_DETAIL}/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { androidx.compose.material3.Text("Detail") }
                composable(Routes.FAVOURITES) { androidx.compose.material3.Text("Favourites") }
            }
        }

        composeTestRule.onNodeWithContentDescription("Favourites").performClick()

        assertEquals(Routes.FAVOURITES, navController.currentDestination?.route)
    }
}
