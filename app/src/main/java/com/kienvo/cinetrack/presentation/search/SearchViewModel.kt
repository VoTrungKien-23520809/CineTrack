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

    init {
        // Debounce 500ms — đợi user gõ xong mới gọi API
        viewModelScope.launch {
            _query
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { q -> runSearch(q) }
        }
    }

    private suspend fun runSearch(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(results = emptyList(), isLoading = false, error = null) }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        repository.searchMovies(query).fold(
            onSuccess = { movies ->
                _uiState.update { it.copy(isLoading = false, results = movies) }
            },
            onFailure = { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        )
    }

    fun onQueryChanged(value: String) {
        _query.value = value
    }

    // Chạy lại tìm kiếm với query hiện tại (dùng cho nút "Thử lại")
    fun retry() {
        viewModelScope.launch { runSearch(_query.value) }
    }
}

data class SearchUiState(
    val isLoading: Boolean = false,
    val results: List<Movie> = emptyList(),
    val error: String? = null
)
