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
import com.example.hw3.ui.screens.HistoryScreen
import com.example.hw3.ui.screens.ProfileScreen
import com.example.hw3.ui.screens.SettingsScreen

@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.GAMES_LIST, modifier = modifier) {

        composable(Routes.GAMES_LIST) {
            val vm: GamesListViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsState()
            val searchQuery by vm.searchQuery.collectAsState()
            GamesListScreen(
                state = uiState.games,
                query = searchQuery,
                isRefreshing = uiState.isRefreshing,
                genres = uiState.genres,
                selectedGenre = uiState.selectedGenre,
                sortOrder = uiState.sortOrder,
                onQueryChange = vm::onQueryChange,
                onGenreSelect = vm::selectGenre,
                onSortChange = vm::setSortOrder,
                onFirstLoad = vm::loadGames,
                onRefresh = vm::refreshGames,
                onRetry = vm::loadGames,
                onGameClick = { id -> navController.navigate("${Routes.GAME_DETAIL}/$id") },
                onFavouritesClick = { navController.navigate(Routes.FAVOURITES) },
                onHistoryClick = { navController.navigate(Routes.HISTORY) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onToggleFavourite = vm::toggleFavourite,
                isFavourite = { id -> uiState.favouriteIds.contains(id) },
                gameStatus = { id -> uiState.statusMap[id] }
            )
        }

        composable(
            route = "${Routes.GAME_DETAIL}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            val vm: GameDetailViewModel = hiltViewModel()
            val state by vm.gameDetailState.collectAsState()
            val gameStatus by vm.gameStatus.collectAsState()
            GameDetailScreen(
                gameId = id,
                state = state,
                gameStatus = gameStatus,
                onLoad = { vm.loadGameDetail(id) },
                onRetry = vm::retryDetail,
                onBack = { navController.popBackStack() },
                onSetStatus = vm::setStatus,
                onClearStatus = vm::clearStatus
            )
        }

        composable(Routes.FAVOURITES) {
            val vm: FavouritesViewModel = hiltViewModel()
            val state by vm.favouritesState.collectAsState()
            FavouritesScreen(state = state, onRemove = vm::removeFavourite, onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            val vm: SettingsViewModel = hiltViewModel()
            val state by vm.settingsState.collectAsState()
            SettingsScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onCacheTtlChange = vm::setCacheTtlHours,
                onHistorySizeChange = vm::setHistoryMaxSize,
                onThemeChange = vm::setThemeMode,
                onClearCache = vm::clearCache
            )
        }

        composable(Routes.HISTORY) {
            val vm: HistoryViewModel = hiltViewModel()
            val state by vm.historyState.collectAsState()
            HistoryScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onClearAll = vm::clearHistory,
                onRemoveEntry = vm::removeEntry,
                onGameClick = { id -> navController.navigate("${Routes.GAME_DETAIL}/$id") }
            )
        }

        composable(Routes.PROFILE) {
            val vm: ProfileViewModel = hiltViewModel()
            val state by vm.profileState.collectAsState()
            ProfileScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onSetUsername = vm::setUsername
            )
        }
    }
}
