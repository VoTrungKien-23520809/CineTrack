package com.kienvo.cinetrack.presentation.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.cinetrack.data.repository.MovieRepositoryImpl
import com.kienvo.cinetrack.domain.model.Movie
import com.kienvo.cinetrack.domain.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository =
        MovieRepositoryImpl(application.applicationContext)

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

            _uiState.update {
                it.copy(
                    isLoading = false,
                    popularMovies = popular.getOrElse { emptyList() },
                    topRatedMovies = topRated.getOrElse { emptyList() },
                    error = popular.exceptionOrNull()?.message
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