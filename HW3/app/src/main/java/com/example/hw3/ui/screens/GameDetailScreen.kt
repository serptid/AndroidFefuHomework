@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.hw3.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import android.content.Intent
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.hw3.data.GameDetail
import com.example.hw3.data.local.UserGameStatus
import com.example.hw3.ui.UiState

@Composable
fun GameDetailScreen(
    gameId: Int,
    state: UiState<GameDetail>,
    gameStatus: UserGameStatus?,
    onLoad: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onSetStatus: (UserGameStatus) -> Unit,
    onClearStatus: () -> Unit
) {
    LaunchedEffect(gameId) { onLoad() }

    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (state is UiState.Success) {
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "${state.data.title}\n${state.data.gameUrl}")
                            }
                            context.startActivity(Intent.createChooser(intent, null))
                        }) {
                            Icon(Icons.Filled.Share, contentDescription = "Поделиться")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        }
    ) { padding ->
        when (state) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is UiState.Error -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onRetry) { Text("Повторить") }
                }
            }

            is UiState.Success -> {
                val game = state.data
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        AsyncImage(
                            model = game.thumbnail,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = game.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AssistChip(
                                onClick = {},
                                label = { Text(game.genre) }
                            )
                            AssistChip(
                                onClick = {},
                                label = { Text(game.platform) }
                            )
                        }

                        Text(
                            text = "Выход: ${game.releaseDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )

                        if (game.developer.isNotBlank()) {
                            Text(
                                text = "Разработчик: ${game.developer}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        if (game.gameUrl.isNotBlank()) {
                            Button(
                                onClick = { uriHandler.openUri(game.gameUrl) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Играть бесплатно")
                            }
                        }

                        if (game.screenshots.isNotEmpty()) {
                            Text(
                                "Скриншоты",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(game.screenshots) { screenshot ->
                                    Box(
                                        modifier = Modifier
                                            .width(200.dp)
                                            .height(120.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        AsyncImage(
                                            model = screenshot.image,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider()

                        Text(
                            text = game.description,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        HorizontalDivider()

                        StatusSelector(
                            currentStatus = gameStatus,
                            onSetStatus = onSetStatus,
                            onClearStatus = onClearStatus
                        )
                    }
                }
            }

            is UiState.Empty -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text("Нет информации") }
            }
        }
    }
}

@Composable
private fun StatusSelector(
    currentStatus: UserGameStatus?,
    onSetStatus: (UserGameStatus) -> Unit,
    onClearStatus: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Мой статус", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            UserGameStatus.entries.forEach { status ->
                FilterChip(
                    selected = currentStatus == status,
                    onClick = {
                        if (currentStatus == status) onClearStatus() else onSetStatus(status)
                    },
                    label = { Text(status.label()) }
                )
            }
        }
    }
}

private fun UserGameStatus.label(): String = when (this) {
    UserGameStatus.INTERESTED -> "Хочу"
    UserGameStatus.PLAYING -> "Играю"
    UserGameStatus.PLAYED -> "Сыграл"
    UserGameStatus.DROPPED -> "Бросил"
}
