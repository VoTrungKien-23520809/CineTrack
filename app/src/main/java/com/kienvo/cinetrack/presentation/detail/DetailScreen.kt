package com.kienvo.cinetrack.presentation.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kienvo.cinetrack.domain.model.CastMember
import com.kienvo.cinetrack.presentation.components.ErrorView
import com.kienvo.cinetrack.presentation.components.ShimmerDetailView
import com.kienvo.cinetrack.presentation.home.MovieCard
import com.kienvo.cinetrack.ui.theme.CinemaGold

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    movieId: Int,
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit = {},
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val movie = uiState.movie
    val context = LocalContext.current
    val draftRating = uiState.draftRating
    val draftNote = uiState.draftNote

    LaunchedEffect(movieId) { viewModel.loadDetail(movieId) }

    when {
        uiState.isLoading -> ShimmerDetailView()
        uiState.error != null -> ErrorView(
            message = "Lỗi: ${uiState.error}",
            onRetry = { viewModel.loadDetail(movieId) }
        )
        movie != null -> {
            Box(Modifier.fillMaxSize()) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                    ) {
                        // Backdrop
                        AsyncImage(
                            model = movie.fullBackdropUrl(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Gradient bottom
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.3f),
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.background
                                        )
                                    )
                                )
                        )

                        // Back button
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .statusBarsPadding()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Black.copy(alpha = 0.5f)
                            ) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                        // Nút xem trailer (mở YouTube) — chỉ hiện khi có trailer
                        uiState.trailerKey?.let { key ->
                            Surface(
                                onClick = {
                                    context.startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://www.youtube.com/watch?v=$key")
                                        )
                                    )
                                },
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.55f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = "Xem trailer",
                                        tint = Color.White,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                        }
                    }


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        // Title
                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 32.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        // Meta row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Rating chip
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CinemaGold.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = CinemaGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = movie.formattedRating(),
                                        color = CinemaGold,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            // Year chip
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = movie.releaseDate.take(4),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Runtime chip (chỉ hiện khi API trả runtime)
                            movie.formattedRuntime()?.let { runtime ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = runtime,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // Genre chips
                        if (movie.genres.isNotEmpty()) {
                            Spacer(Modifier.height(12.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                movie.genres.forEach { genre ->
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = genre,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // ── Action buttons ────────────────────────
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Watchlist button
                            val isInWatchlist = uiState.isInWatchlist
                            Button(
                                onClick = { viewModel.toggleWatchlist() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isInWatchlist)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isInWatchlist)
                                        Color.White
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = if (isInWatchlist)
                                        Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    if (isInWatchlist) "Đã lưu" else "Lưu lại",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Watched button
                            Button(
                                onClick = { viewModel.toggleWatched() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (uiState.isWatched)
                                        Color(0xFF1DB954)
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (uiState.isWatched)
                                        Color.White
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = if (uiState.isWatched)
                                        Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    if (uiState.isWatched) "Đã xem" else "Chưa xem",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Text(
                            text = "Nội dung phim",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = movie.overview.ifEmpty { "Chưa có mô tả." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 24.sp
                        )

                        // ── Đánh giá & ghi chú (chỉ hiện khi đã xem) ────
                        if (uiState.isWatched) {
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = "Đánh giá của bạn",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                (1..5).forEach { star ->
                                    IconButton(
                                        onClick = { viewModel.setDraftRating(star) },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "$star sao",
                                            tint = if (draftRating != null && star <= draftRating)
                                                CinemaGold
                                            else
                                                MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(
                                value = draftNote,
                                onValueChange = { viewModel.setDraftNote(it) },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Ghi chú của bạn") },
                                placeholder = { Text("Cảm nhận về bộ phim...") },
                                minLines = 2,
                                maxLines = 4,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.saveRatingAndNote() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Lưu đánh giá", fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // ── Diễn viên ─────────────────────────────
                        if (uiState.cast.isNotEmpty()) {
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = "Diễn viên",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(uiState.cast, key = { it.id }) { cast ->
                                    CastCard(cast)
                                }
                            }
                        }

                        // ── Phim đề xuất ──────────────────────────
                        if (uiState.recommendations.isNotEmpty()) {
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = "Có thể bạn thích",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(uiState.recommendations, key = { it.id }) { rec ->
                                    Box(Modifier.width(130.dp)) {
                                        MovieCard(movie = rec, onClick = { onMovieClick(rec.id) })
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CastCard(cast: CastMember) {
    Column(
        modifier = Modifier.width(88.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (cast.profileUrl != null) {
            AsyncImage(
                model = cast.profileUrl,
                contentDescription = cast.name,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = cast.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        if (cast.character.isNotBlank()) {
            Text(
                text = cast.character,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}