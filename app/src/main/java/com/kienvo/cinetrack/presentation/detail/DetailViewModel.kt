package com.kienvo.cinetrack.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.cinetrack.domain.model.Movie
import com.kienvo.cinetrack.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    // Tracks the Flow collectors so we cancel them khi loadDetail được gọi lại
    private var observeJob: Job? = null

    fun loadDetail(movieId: Int) {
        // Huỷ collectors cũ để tránh leak khi movieId đổi
        observeJob?.cancel()

        // 1. Gọi API lấy chi tiết phim (one-shot, không collect)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = repository.getMovieDetail(movieId)
            val movie = result.getOrNull()

            if (movie == null) {
                _uiState.update {
                    it.copy(isLoading = false, error = result.exceptionOrNull()?.message)
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, movie = movie, error = null)
                }
            }
        }

        // 2. Quan sát trạng thái watchlist / đã xem trong 1 job riêng có thể huỷ
        observeJob = viewModelScope.launch {
            launch {
                repository.isInWatchlist(movieId).collect { inWatchlist ->
                    _uiState.update { it.copy(isInWatchlist = inWatchlist) }
                }
            }
            launch {
                repository.getWatched().collect { watchedMovies ->
                    val isWatched = watchedMovies.any { it.id == movieId }
                    _uiState.update { it.copy(isWatched = isWatched) }
                }
            }
        }
    }

    fun toggleWatchlist() {
        val movie = _uiState.value.movie ?: return
        viewModelScope.launch {
            if (_uiState.value.isInWatchlist) {
                repository.removeFromWatchlist(movie)
            } else {
                repository.addToWatchlist(movie)
            }
        }
    }

    fun toggleWatched() {
        val movie = _uiState.value.movie ?: return
        val currentlyWatched = _uiState.value.isWatched
        viewModelScope.launch {
            if (!_uiState.value.isInWatchlist) {
                repository.addToWatchlist(movie)
            }
            repository.markAsWatched(movie.id, !currentlyWatched)

            _uiState.update { it.copy(isWatched = !currentlyWatched, isInWatchlist = true) }
        }
    }
}

data class DetailUiState(
    val isLoading: Boolean = false,
    val movie: Movie? = null,
    val isInWatchlist: Boolean = false,
    val isWatched: Boolean = false,
    val error: String? = null
)
