package com.kienvo.cinetrack.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.cinetrack.domain.model.Movie
import com.kienvo.cinetrack.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchPage = 1
    private var lastQuery = ""

    init {
        viewModelScope.launch {
            _query
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { q -> runSearch(q, reset = true) }
        }
    }

    private suspend fun runSearch(query: String, reset: Boolean) {
        if (query.isBlank()) {
            _uiState.update { it.copy(results = emptyList(), isLoading = false, error = null) }
            return
        }
        if (reset) {
            searchPage = 1
            lastQuery = query
        }
        val isFirstPage = searchPage == 1
        _uiState.update {
            if (isFirstPage) it.copy(isLoading = true, error = null)
            else it.copy(isLoadingMore = true)
        }
        repository.searchMovies(query, searchPage).fold(
            onSuccess = { movies ->
                _uiState.update {
                    if (isFirstPage) it.copy(isLoading = false, results = movies)
                    else it.copy(isLoadingMore = false, results = it.results + movies)
                }
            },
            onFailure = { e ->
                _uiState.update { it.copy(isLoading = false, isLoadingMore = false, error = e.message) }
            }
        )
    }

    fun onQueryChanged(value: String) {
        _query.value = value
    }

    fun loadMore() {
        if (_uiState.value.isLoadingMore || lastQuery.isBlank()) return
        searchPage++
        viewModelScope.launch { runSearch(lastQuery, reset = false) }
    }

    fun retry() {
        viewModelScope.launch { runSearch(_query.value, reset = true) }
    }
}

data class SearchUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val results: List<Movie> = emptyList(),
    val error: String? = null
)