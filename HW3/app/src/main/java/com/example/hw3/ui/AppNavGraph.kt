package com.example.hw3.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hw3.ui.screens.FavouritesScreen
import com.example.hw3.ui.screens.GameDetailScreen
import com.example.hw3.ui.screens.GamesListScreen

@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.GAMES_LIST,
        modifier = modifier
    ) {
        composable(Routes.GAMES_LIST) {
            val vm: GamesListViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsState()
            val searchQuery by vm.searchQuery.collectAsState()
            GamesListScreen(
                state = uiState.games,
                query = searchQuery,
                isRefreshing = uiState.isRefreshing,
                onQueryChange = vm::onQueryChange,
                onFirstLoad = vm::loadGames,
                onRefresh = vm::refreshGames,
                onRetry = vm::loadGames,
                onGameClick = { id -> navController.navigate("${Routes.GAME_DETAIL}/$id") },
                onFavouritesClick = { navController.navigate(Routes.FAVOURITES) },
                onToggleFavourite = vm::toggleFavourite,
                isFavourite = { id -> uiState.favouriteIds.contains(id) }
            )
        }

        composable(
            route = "${Routes.GAME_DETAIL}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            val vm: GameDetailViewModel = hiltViewModel()
            val state by vm.gameDetailState.collectAsState()
            GameDetailScreen(
                gameId = id,
                state = state,
                onLoad = { vm.loadGameDetail(id) },
                onRetry = vm::retryDetail,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.FAVOURITES) {
            val vm: FavouritesViewModel = hiltViewModel()
            val state by vm.favouritesState.collectAsState()
            FavouritesScreen(
                state = state,
                onRemove = vm::removeFavourite,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
