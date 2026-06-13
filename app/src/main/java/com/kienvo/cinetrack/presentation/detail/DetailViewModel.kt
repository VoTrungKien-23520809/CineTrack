package com.kienvo.cinetrack.presentation.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.cinetrack.data.repository.MovieRepositoryImpl
import com.kienvo.cinetrack.domain.model.Movie
import com.kienvo.cinetrack.domain.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository =
        MovieRepositoryImpl(application.applicationContext)

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadDetail(movieId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = repository.getMovieDetail(movieId)
            val movie = result.getOrNull()

            if (movie != null) {
                viewModelScope.launch {
                    repository.getWatched().collect { watchedMovies ->
                        val isWatched = watchedMovies.any { it.id == movieId }
                        _uiState.update { it.copy(isWatched = isWatched) }
                    }
                }

                repository.isInWatchlist(movieId).collect { inWatchlist ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            movie = movie,
                            isInWatchlist = inWatchlist,
                            error = null
                        )
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message
                    )
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