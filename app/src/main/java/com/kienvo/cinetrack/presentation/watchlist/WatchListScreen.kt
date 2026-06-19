package com.kienvo.cinetrack.presentation.watchlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kienvo.cinetrack.domain.model.Movie
import com.kienvo.cinetrack.ui.theme.CinemaGold

@Composable
fun WatchlistScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: WatchlistViewModel = hiltViewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val wantToWatch by viewModel.wantToWatch.collectAsStateWithLifecycle()
    val watched by viewModel.watched.collectAsStateWithLifecycle()
    val pendingDeleteMovie by viewModel.pendingDeleteMovie.collectAsStateWithLifecycle()
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(pendingDeleteMovie) {
        val movie = pendingDeleteMovie ?: return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = "Đã xoá \"${movie.title}\"",
            actionLabel = "Hoàn tác",
            duration = SnackbarDuration.Short
        )
        when (result) {
            SnackbarResult.ActionPerformed -> viewModel.undoDelete()
            SnackbarResult.Dismissed -> viewModel.confirmDelete()
        }
    }

    val tabs = listOf("Muốn xem", "Đã xem")
    val currentList = (if (selectedTab == 0) wantToWatch else watched)
        .filter { it.id != pendingDeleteMovie?.id }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Text(
                text = "Watchlist",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = { Text(title) }
                    )
                }
            }

            // Sort chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SortOption.entries) { option ->
                    FilterChip(
                        selected = sortBy == option,
                        onClick = { viewModel.setSortOption(option) },
                        label = { Text(option.label) }
                    )
                }
            }

            if (currentList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (selectedTab == 0) "Chưa có phim muốn xem 🍿" else "Chưa có phim đã xem ✅",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(currentList, key = { it.id }) { movie ->
                        SwipeableWatchlistCard(
                            movie = movie,
                            onClick = { onMovieClick(movie.id) },
                            onDelete = { viewModel.softDelete(movie) }
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableWatchlistCard(
    movie: Movie,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Xóa",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    ) {
        WatchlistMovieCard(movie = movie, onClick = onClick, onDelete = onDelete)
    }
}

@Composable
fun WatchlistMovieCard(
    movie: Movie,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(Modifier.padding(12.dp)) {
            AsyncImage(
                model = movie.fullPosterUrl(),
                contentDescription = null,
                modifier = Modifier
                    .width(70.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = movie.overview,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = CinemaGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = movie.formattedRating(),
                        style = MaterialTheme.typography.labelMedium,
                        color = CinemaGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = movie.releaseDate.take(4),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    // Hiện sao người dùng đánh giá nếu có
                    movie.userRating?.let { rating ->
                        Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        repeat(rating) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Xóa",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}