package com.kienvo.cinetrack.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kienvo.cinetrack.presentation.components.ErrorView
import com.kienvo.cinetrack.presentation.components.MovieGrid
import com.kienvo.cinetrack.presentation.components.ShimmerMovieGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Phổ biến", "Top rated")

    val gridState = rememberLazyGridState()
    val endReached by remember {
        derivedStateOf {
            val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = gridState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 4
        }
    }

    LaunchedEffect(endReached) {
        if (endReached) {
            if (selectedTab == 0) viewModel.loadMorePopular()
            else viewModel.loadMoreTopRated()
        }
    }

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when {
            uiState.isLoading -> ShimmerMovieGrid()
            uiState.error != null -> ErrorView(uiState.error!!, onRetry = { viewModel.loadMovies() })
            else -> {
                val movies = if (selectedTab == 0) uiState.popularMovies else uiState.topRatedMovies
                val isLoadingMore = if (selectedTab == 0) uiState.isLoadingMorePopular else uiState.isLoadingMoreTopRated
                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { viewModel.refresh() }
                ) {
                    MovieGrid(
                        movies = movies,
                        onMovieClick = onMovieClick,
                        isLoadingMore = isLoadingMore,
                        state = gridState
                    )
                }
            }
        }
    }
}