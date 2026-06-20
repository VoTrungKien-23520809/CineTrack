package com.kienvo.cinetrack.presentation.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kienvo.cinetrack.presentation.components.ErrorView
import com.kienvo.cinetrack.presentation.components.MovieCard
import com.kienvo.cinetrack.presentation.components.ShimmerMovieCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    onProfileClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    // Chỉ 2 tab, chia đều ngang
    val tabs = listOf("Phổ biến", "Top Rated")
    val movies = if (selectedTab == 0) uiState.popularMovies else uiState.topRatedMovies
    val isLoadingMore = if (selectedTab == 0) uiState.isLoadingMorePopular else uiState.isLoadingMoreTopRated

    val listState = rememberLazyListState()
    val endReached by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 6
        }
    }

    LaunchedEffect(selectedTab) { listState.animateScrollToItem(0) }
    LaunchedEffect(endReached) {
        if (endReached && !isLoadingMore) {
            if (selectedTab == 0) viewModel.loadMorePopular()
            else viewModel.loadMoreTopRated()
        }
    }

    // Detect khi TopBar (item index 0) đã scroll ra khỏi tầm nhìn
    // → sticky TabBar cần tự thêm status bar inset để không bị che
    val isTopBarHidden by remember {
        derivedStateOf { listState.firstVisibleItemIndex >= 1 }
    }

    val gridMovies = if (movies.size > 5) movies.drop(5) else emptyList()
    val moviePairs = gridMovies.chunked(2)

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {

            item(key = "topbar") {
                HomeTopBar(onProfileClick = onProfileClick)
            }

            stickyHeader(key = "tabs") {
                HomeTabBar(
                    tabs          = tabs,
                    selectedTab   = selectedTab,
                    onTabSelected = { selectedTab = it },
                    isTopBarHidden = isTopBarHidden
                )
            }

            if (movies.isNotEmpty()) {
                item(key = "carousel") {
                    FeaturedCarousel(
                        movies = movies.take(5),
                        onMovieClick = onMovieClick
                    )
                }
            }

            if (uiState.isLoading) {
                item(key = "shimmer") {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        repeat(3) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(Modifier.weight(1f)) { ShimmerMovieCard() }
                                Box(Modifier.weight(1f)) { ShimmerMovieCard() }
                            }
                        }
                    }
                }
            } else if (uiState.error != null) {
                item(key = "error") {
                    ErrorView(message = uiState.error!!, onRetry = { viewModel.loadMovies() })
                }
            } else {
                if (gridMovies.isNotEmpty()) {
                    item(key = "section_header_$selectedTab") {
                        GridSectionHeader(
                            title = if (selectedTab == 0) "Phim Đề Xuất Cho Bạn" else "Phim Đánh Giá Cao",
                            subtitle = if (selectedTab == 0) "Những bộ phim hot nhất hiện tại" else "Được khán giả yêu thích nhất"
                        )
                    }

                    items(
                        count = moviePairs.size,
                        key = { "pair_${selectedTab}_$it" }
                    ) { pairIndex ->
                        val pair = moviePairs[pairIndex]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                MovieCard(
                                    movie = pair[0],
                                    onClick = { onMovieClick(pair[0].id) }
                                )
                            }
                            if (pair.size > 1) {
                                Box(modifier = Modifier.weight(1f)) {
                                    MovieCard(
                                        movie = pair[1],
                                        onClick = { onMovieClick(pair[1].id) }
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    if (isLoadingMore) {
                        item(key = "loading_more") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                }
            }

            item(key = "bottom_spacer") { Spacer(Modifier.navigationBarsPadding()) }
        }
    }
}

// ─── GridSectionHeader ────────────────────────────────────────────────────────

@Composable
private fun GridSectionHeader(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─── HomeTopBar ───────────────────────────────────────────────────────────────

@Composable
private fun HomeTopBar(onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Padding hệ thống status bar — chỉ apply một lần ở đây, không có khoảng trống dư
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── Logo: tròn đỏ + icon clapperboard phim ─────────────────────────────
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Default.LocalMovies,
                contentDescription = null,
                tint               = Color.White,
                modifier           = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text       = "CineTrack",
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.weight(1f))

        // ── Avatar tròn xám ─────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Default.AccountCircle,
                contentDescription = "Hồ sơ",
                modifier           = Modifier.size(28.dp),
                tint               = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─── HomeTabBar ───────────────────────────────────────────────────────────────

@Composable
private fun HomeTabBar(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    isTopBarHidden: Boolean = false
) {
    // Lấy chiều cao status bar thực tế (px → dp)
    val statusBarHeightDp: Dp = with(LocalDensity.current) {
        WindowInsets.statusBars.getTop(this).toDp()
    }

    // Animate khoảng trống phía trên:
    //  • isTopBarHidden = true  → cần đồng status bar height để đẩy nội dung xuống
    //  • isTopBarHidden = false → TopBar đang hiện, không cần thêm gì
    val animatedTopPadding by animateDpAsState(
        targetValue   = if (isTopBarHidden) statusBarHeightDp else 0.dp,
        animationSpec = tween(durationMillis = 220),
        label         = "tab_status_bar_inset"
    )

    Surface(
        color    = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Spacer animate — chỉ xuất hiện khi tab bar bị sticky ở top
            if (animatedTopPadding > 0.dp) {
                Spacer(Modifier.height(animatedTopPadding))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp)
            ) {
                // weight(1f) cho mỗi tab → chia đều toàn bộ chiều ngang
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTabSelected(index) }
                            .padding(top = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text       = title,
                            style      = MaterialTheme.typography.titleSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color      = if (isSelected)
                                             MaterialTheme.colorScheme.primary
                                         else
                                             MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize   = 15.sp
                        )
                        Spacer(Modifier.height(8.dp))

                        // Underline đỏ animate
                        val underlineWidth by animateDpAsState(
                            targetValue   = if (isSelected) 32.dp else 0.dp,
                            animationSpec = tween(250),
                            label         = "ul_$index"
                        )
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(underlineWidth)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(1.5.dp)
                                )
                        )
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }

            // Divider mỏng
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            )
        }
    }
}