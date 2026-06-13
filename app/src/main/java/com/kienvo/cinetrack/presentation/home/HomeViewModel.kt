package com.kienvo.cinetrack.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.cinetrack.domain.model.Movie
import com.kienvo.cinetrack.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val popular = repository.getPopularMovies()
            val topRated = repository.getTopRatedMovies()

            // Combine errors from both calls
            val error = popular.exceptionOrNull()?.message
                ?: topRated.exceptionOrNull()?.message

            _uiState.update {
                it.copy(
                    isLoading = false,
                    popularMovies = popular.getOrElse { emptyList() },
                    topRatedMovies = topRated.getOrElse { emptyList() },
                    error = error
                )
            }
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val popularMovies: List<Movie> = emptyList(),
    val topRatedMovies: List<Movie> = emptyList(),
    val error: String? = null
)