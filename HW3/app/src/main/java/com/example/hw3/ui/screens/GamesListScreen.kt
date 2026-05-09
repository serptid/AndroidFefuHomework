@file:OptIn(ExperimentalMaterial3Api::class, androidx.compose.material.ExperimentalMaterialApi::class)

package com.example.hw3.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.hw3.data.Game
import com.example.hw3.data.local.UserGameStatus
import com.example.hw3.ui.SortOrder
import com.example.hw3.ui.UiState

@Composable
fun GamesListScreen(
    state: UiState<List<Game>>,
    query: String,
    isRefreshing: Boolean,
    genres: List<String>,
    selectedGenre: String?,
    sortOrder: SortOrder,
    onQueryChange: (String) -> Unit,
    onGenreSelect: (String?) -> Unit,
    onSortChange: (SortOrder) -> Unit,
    onFirstLoad: () -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onGameClick: (Int) -> Unit,
    onFavouritesClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onToggleFavourite: (Game) -> Unit,
    isFavourite: (Int) -> Boolean,
    gameStatus: (Int) -> UserGameStatus?
) {
    LaunchedEffect(Unit) { onFirstLoad() }

    val pullState = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = onRefresh)
    var sortExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Free Games", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Filled.Person, contentDescription = "Профиль")
                    }
                },
                actions = {
                    IconButton(onClick = onFavouritesClick) {
                        Icon(Icons.Filled.Star, contentDescription = "Избранное")
                    }
                    IconButton(onClick = onHistoryClick) {
                        Icon(Icons.Filled.History, contentDescription = "История")
                    }
                    Box {
                        IconButton(onClick = { sortExpanded = true }) {
                            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Сортировка")
                        }
                        DropdownMenu(
                            expanded = sortExpanded,
                            onDismissRequest = { sortExpanded = false }
                        ) {
                            listOf(
                                SortOrder.BY_TITLE to "По названию",
                                SortOrder.BY_DATE to "По дате выхода",
                                SortOrder.BY_GENRE to "По жанру"
                            ).forEach { (order, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = { onSortChange(order); sortExpanded = false },
                                    leadingIcon = {
                                        if (sortOrder == order) {
                                            Icon(Icons.Filled.Check, contentDescription = null)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Filled.Settings, contentDescription = "Настройки")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (state is UiState.Success && state.data.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { onGameClick(state.data.random().id) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Shuffle, contentDescription = "Случайная игра")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .pullRefresh(pullState)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp, bottom = 4.dp)
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        placeholder = { Text("Поиск игр...") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { onQueryChange("") }) {
                                    Icon(Icons.Filled.Clear, contentDescription = null)
                                }
                            }
                        }
                    )

                    if (genres.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        GenreFilterRow(genres = genres, selectedGenre = selectedGenre, onSelect = onGenreSelect)
                    }
                }

                when (state) {
                    is UiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    is UiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(state.message, style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = onRetry) { Text("Повторить") }
                        }
                    }

                    is UiState.Empty -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Ничего не найдено", style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    }

                    is UiState.Success -> {
                        GamesList(
                            games = state.data,
                            onGameClick = onGameClick,
                            onToggleFavourite = onToggleFavourite,
                            isFavourite = isFavourite,
                            gameStatus = gameStatus
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
private fun GenreFilterRow(genres: List<String>, selectedGenre: String?, onSelect: (String?) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        item {
            FilterChip(selected = selectedGenre == null, onClick = { onSelect(null) }, label = { Text("Все") })
        }
        items(genres) { genre ->
            FilterChip(
                selected = selectedGenre == genre,
                onClick = { onSelect(if (selectedGenre == genre) null else genre) },
                label = { Text(genre) }
            )
        }
    }
}

@Composable
private fun GamesList(
    games: List<Game>,
    onGameClick: (Int) -> Unit,
    onToggleFavourite: (Game) -> Unit,
    isFavourite: (Int) -> Boolean,
    gameStatus: (Int) -> UserGameStatus?
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(games) { game ->
            GameCard(
                game = game,
                isFavourite = isFavourite(game.id),
                status = gameStatus(game.id),
                onGameClick = { onGameClick(game.id) },
                onToggleFavourite = { onToggleFavourite(game) }
            )
        }
    }
}

@Composable
private fun GameCard(
    game: Game,
    isFavourite: Boolean,
    status: UserGameStatus?,
    onGameClick: () -> Unit,
    onToggleFavourite: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(100.dp).clickable(onClick = onGameClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
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
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(game.title, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("${game.genre} · ${game.platform}", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (status != null) StatusBadge(status)
            }

            IconButton(onClick = onToggleFavourite, modifier = Modifier.padding(end = 4.dp)) {
                Icon(
                    imageVector = if (isFavourite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Избранное",
                    tint = if (isFavourite) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
internal fun StatusBadge(status: UserGameStatus) {
    val (label, color) = when (status) {
        UserGameStatus.INTERESTED -> "Хочу" to Color(0xFF4CAF50)
        UserGameStatus.PLAYING -> "Играю" to Color(0xFF2196F3)
        UserGameStatus.PLAYED -> "Сыграл" to Color(0xFF9C27B0)
        UserGameStatus.DROPPED -> "Бросил" to Color(0xFFF44336)
    }
    Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = 0.18f)) {
        Text(text = label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.SemiBold)
    }
}
