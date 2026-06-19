package com.kienvo.cinetrack.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.cinetrack.domain.model.CastMember
import com.kienvo.cinetrack.domain.model.Movie
import com.kienvo.cinetrack.domain.model.MovieDetail
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

    private var observeJob: Job? = null
    private var draftInitialized = false

    fun loadDetail(movieId: Int) {
        observeJob?.cancel()
        draftInitialized = false

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = repository.getMovieDetail(movieId)
            val movie = result.getOrNull()

            if (movie == null) {
                _uiState.update {
                    it.copy(isLoading = false, error = result.exceptionOrNull()?.message)
                }
            } else {
                _uiState.update { it.copy(isLoading = false, movie = movie, error = null) }
            }
        }

        viewModelScope.launch {
            val cast = repository.getMovieCredits(movieId).getOrDefault(emptyList())
            _uiState.update { it.copy(cast = cast) }
        }
        viewModelScope.launch {
            val videos = repository.getMovieVideos(movieId).getOrDefault(emptyList())
            val trailer = videos.firstOrNull { it.isYoutube && it.type.equals("Trailer", true) }
                ?: videos.firstOrNull { it.isYoutube }
            _uiState.update { it.copy(trailerKey = trailer?.key) }
        }
        viewModelScope.launch {
            val recommendations = repository.getRecommendations(movieId).getOrDefault(emptyList())
            _uiState.update { it.copy(recommendations = recommendations) }
        }

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
            launch {
                repository.getWatchlistEntry(movieId).collect { entry ->
                    if (!draftInitialized && entry != null) {
                        _uiState.update {
                            it.copy(
                                draftRating = entry.userRating,
                                draftNote = entry.userNote ?: ""
                            )
                        }
                        draftInitialized = true
                    }
                }
            }
        }
    }

    fun toggleWatchlist() {
        val movie = _uiState.value.movie?.toMovie() ?: return
        viewModelScope.launch {
            if (_uiState.value.isInWatchlist) {
                repository.removeFromWatchlist(movie)
            } else {
                repository.addToWatchlist(movie)
            }
        }
    }

    fun toggleWatched() {
        val movie = _uiState.value.movie?.toMovie() ?: return
        val currentlyWatched = _uiState.value.isWatched
        viewModelScope.launch {
            if (!_uiState.value.isInWatchlist) {
                repository.addToWatchlist(movie)
            }
            repository.markAsWatched(movie.id, !currentlyWatched)
            _uiState.update { it.copy(isWatched = !currentlyWatched, isInWatchlist = true) }
        }
    }

    fun setDraftRating(rating: Int) {
        val newRating = if (_uiState.value.draftRating == rating) null else rating
        _uiState.update { it.copy(draftRating = newRating) }
    }

    fun setDraftNote(note: String) {
        _uiState.update { it.copy(draftNote = note) }
    }

    fun saveRatingAndNote() {
        val movieId = _uiState.value.movie?.id ?: return
        viewModelScope.launch {
            repository.updateRatingAndNote(
                movieId = movieId,
                rating = _uiState.value.draftRating,
                note = _uiState.value.draftNote.trim().ifEmpty { null }
            )
        }
    }
}

data class DetailUiState(
    val isLoading: Boolean = false,
    val movie: MovieDetail? = null,
    val isInWatchlist: Boolean = false,
    val isWatched: Boolean = false,
    val cast: List<CastMember> = emptyList(),
    val trailerKey: String? = null,
    val recommendations: List<Movie> = emptyList(),
    val error: String? = null,
    val draftRating: Int? = null,
    val draftNote: String = ""
)