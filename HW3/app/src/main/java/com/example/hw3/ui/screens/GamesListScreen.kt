@file:OptIn(ExperimentalMaterial3Api::class, androidx.compose.material.ExperimentalMaterialApi::class)

package com.example.hw3.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hw3.data.Game
import com.example.hw3.ui.UiState

@Composable
fun GamesListScreen(
    state: UiState<List<Game>>,
    query: String,
    isRefreshing: Boolean,
    onQueryChange: (String) -> Unit,
    onFirstLoad: () -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onGameClick: (Int) -> Unit,
    onFavouritesClick: () -> Unit,
    onToggleFavourite: (Game) -> Unit,
    isFavourite: (Int) -> Boolean
) {
    LaunchedEffect(Unit) { onFirstLoad() }

    val pullState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Free To Play Games") },
                actions = {
                    IconButton(onClick = onFavouritesClick) {
                        Icon(Icons.Filled.Star, contentDescription = "Favourites")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .pullRefresh(pullState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Search") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                when (state) {
                    is UiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    is UiState.Error -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(state.message)
                            Button(onClick = onRetry) { Text("Retry") }
                        }
                    }

                    is UiState.Empty -> {
                        Text("Nothing found")
                    }

                    is UiState.Success -> {
                        GamesList(
                            games = state.data,
                            onGameClick = onGameClick,
                            onToggleFavourite = onToggleFavourite,
                            isFavourite = isFavourite
                        )
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun GamesList(
    games: List<Game>,
    onGameClick: (Int) -> Unit,
    onToggleFavourite: (Game) -> Unit,
    isFavourite: (Int) -> Boolean
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(games) { game ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onGameClick(game.id) }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(game.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(game.genre)
                        Text(game.platform)
                    }

                    IconButton(onClick = { onToggleFavourite(game) }) {
                        Icon(
                            imageVector = if (isFavourite(game.id)) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favourite"
                        )
                    }
                }
            }
        }
    }
}
