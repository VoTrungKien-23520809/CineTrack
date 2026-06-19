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

    private var popularPage = 1
    private var topRatedPage = 1

    init {
        loadMovies()
    }

    fun loadMovies() {
        popularPage = 1
        topRatedPage = 1
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val popular = repository.getPopularMovies(page = 1)
            val topRated = repository.getTopRatedMovies(page = 1)

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

    fun refresh() {
        if (_uiState.value.isRefreshing) return
        popularPage = 1
        topRatedPage = 1
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val popular = repository.getPopularMovies(1)
            val topRated = repository.getTopRatedMovies(1)
            val prevPopular = _uiState.value.popularMovies
            val prevTopRated = _uiState.value.topRatedMovies
            _uiState.update {
                it.copy(
                    isRefreshing = false,
                    popularMovies = popular.getOrElse { prevPopular },
                    topRatedMovies = topRated.getOrElse { prevTopRated }
                )
            }
        }
    }

    fun loadMorePopular() {
        if (_uiState.value.isLoadingMorePopular) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMorePopular = true) }
            popularPage++
            val more = repository.getPopularMovies(popularPage).getOrElse { emptyList() }
            _uiState.update {
                it.copy(
                    isLoadingMorePopular = false,
                    popularMovies = it.popularMovies + more
                )
            }
        }
    }

    fun loadMoreTopRated() {
        if (_uiState.value.isLoadingMoreTopRated) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMoreTopRated = true) }
            topRatedPage++
            val more = repository.getTopRatedMovies(topRatedPage).getOrElse { emptyList() }
            _uiState.update {
                it.copy(
                    isLoadingMoreTopRated = false,
                    topRatedMovies = it.topRatedMovies + more
                )
            }
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val popularMovies: List<Movie> = emptyList(),
    val topRatedMovies: List<Movie> = emptyList(),
    val isLoadingMorePopular: Boolean = false,
    val isLoadingMoreTopRated: Boolean = false,
    val error: String? = null
)