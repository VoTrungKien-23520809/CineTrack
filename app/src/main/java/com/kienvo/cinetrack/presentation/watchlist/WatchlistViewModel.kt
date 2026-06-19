package com.kienvo.cinetrack.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.cinetrack.domain.model.Movie
import com.kienvo.cinetrack.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOption(val label: String) {
    ByDateAdded("Ngày thêm"),
    ByTitle("Tên phim"),
    ByRating("Đánh giá"),
    ByYear("Năm")
}

private fun List<Movie>.applySortOption(option: SortOption) = when (option) {
    SortOption.ByDateAdded -> this
    SortOption.ByTitle -> sortedBy { it.title }
    SortOption.ByRating -> sortedByDescending { it.voteAverage }
    SortOption.ByYear -> sortedByDescending { it.releaseDate }
}

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _sortBy = MutableStateFlow(SortOption.ByDateAdded)
    val sortBy: StateFlow<SortOption> = _sortBy.asStateFlow()

    val wantToWatch: StateFlow<List<Movie>> = combine(
        repository.getWantToWatch(), _sortBy
    ) { movies, sort -> movies.applySortOption(sort) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watched: StateFlow<List<Movie>> = combine(
        repository.getWatched(), _sortBy
    ) { movies, sort -> movies.applySortOption(sort) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _pendingDeleteMovie = MutableStateFlow<Movie?>(null)
    val pendingDeleteMovie: StateFlow<Movie?> = _pendingDeleteMovie.asStateFlow()

    fun selectTab(index: Int) { _selectedTab.value = index }

    fun setSortOption(option: SortOption) { _sortBy.value = option }

    fun softDelete(movie: Movie) { _pendingDeleteMovie.value = movie }

    fun undoDelete() { _pendingDeleteMovie.value = null }

    fun confirmDelete() {
        val movie = _pendingDeleteMovie.value ?: return
        _pendingDeleteMovie.value = null
        viewModelScope.launch { repository.removeFromWatchlist(movie) }
    }
}