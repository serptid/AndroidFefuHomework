@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.hw3.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hw3.data.Game
import com.example.hw3.ui.UiState

@Composable
fun FavouritesScreen(
    state: UiState<List<Game>>,
    onRemove: (Game) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favourites") },
                navigationIcon = { IconButton(onClick = onBack) { Text("<") } }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (state) {
                is UiState.Empty -> Text("No favourites yet")
                is UiState.Success -> FavouritesList(games = state.data, onRemove = onRemove)
                else -> {}
            }
        }
    }
}

@Composable
private fun FavouritesList(
    games: List<Game>,
    onRemove: (Game) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(games) { game ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(game.title, style = MaterialTheme.typography.titleMedium)
                        Text(game.genre)
                    }
                    Text(
                        text = "Remove",
                        modifier = Modifier.clickable { onRemove(game) }
                    )
                }
            }
        }
    }
}
