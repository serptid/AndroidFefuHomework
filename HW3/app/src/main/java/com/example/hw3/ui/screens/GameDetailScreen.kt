@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.hw3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hw3.ui.UiState
import com.example.hw3.data.GameDetail

@Composable
fun GameDetailScreen(
    gameId: Int,
    state: UiState<GameDetail>,
    onLoad: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(gameId) { onLoad() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game details") },
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
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }

                is UiState.Error -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(state.message)
                        Button(onClick = onRetry) { Text("Retry") }
                    }
                }

                is UiState.Success -> {
                    val game = state.data
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(game.title, style = MaterialTheme.typography.titleLarge)
                        Text("Genre: ${game.genre}")
                        Text("Platform: ${game.platform}")
                        Text("Release: ${game.releaseDate}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(game.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                else -> {}
            }
        }
    }
}
