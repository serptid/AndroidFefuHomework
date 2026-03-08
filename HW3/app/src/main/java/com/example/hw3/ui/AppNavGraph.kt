package com.example.hw3.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hw3.ui.screens.FavouritesScreen
import com.example.hw3.ui.screens.GameDetailScreen
import com.example.hw3.ui.screens.GamesListScreen

@Composable
fun AppNavGraph(
    viewModel: GamesViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.GAMES_LIST,
        modifier = modifier
    ) {
        composable(Routes.GAMES_LIST) {
            GamesListScreen(
                state = viewModel.gamesState,
                query = viewModel.query,
                isRefreshing = viewModel.isRefreshing,
                onQueryChange = viewModel::onQueryChange,
                onFirstLoad = viewModel::loadGames,
                onRefresh = viewModel::refreshGames,
                onRetry = viewModel::loadGames,
                onGameClick = { id -> navController.navigate("${Routes.GAME_DETAIL}/$id") },
                onFavouritesClick = { navController.navigate(Routes.FAVOURITES) },
                onToggleFavourite = viewModel::toggleFavourite,
                isFavourite = viewModel::isFavourite
            )
        }

        composable(
            route = "${Routes.GAME_DETAIL}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            GameDetailScreen(
                gameId = id,
                state = viewModel.gameDetailState,
                onLoad = { viewModel.loadGameDetail(id) },
                onRetry = viewModel::retryDetail,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.FAVOURITES) {
            FavouritesScreen(
                state = viewModel.favouritesState,
                onRemove = viewModel::toggleFavourite,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
