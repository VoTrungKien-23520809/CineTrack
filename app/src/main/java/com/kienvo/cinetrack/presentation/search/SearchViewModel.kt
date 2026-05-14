package com.kienvo.cinetrack.presentation.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.cinetrack.data.remote.RetrofitInstance
import com.kienvo.cinetrack.data.remote.ApiKeyProvider
import com.kienvo.cinetrack.domain.model.Movie
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val api = RetrofitInstance.api
    private val apiKey by lazy { ApiKeyProvider.getApiKey(getApplication()) }

    val query = MutableStateFlow("")

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        // Debounce 500ms — đợi user gõ xong mới gọi API
        viewModelScope.launch {
            query
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { q ->
                    if (q.isBlank()) {
                        _uiState.update { it.copy(results = emptyList(), isLoading = false) }
                        return@collectLatest
                    }
                    _uiState.update { it.copy(isLoading = true, error = null) }
                    try {
                        val results = api.searchMovies(q, apiKey).results.map { it.toDomain() }
                        _uiState.update { it.copy(isLoading = false, results = results) }
                    } catch (e: Exception) {
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                }
        }
    }
}

data class SearchUiState(
    val isLoading: Boolean = false,
    val results: List<Movie> = emptyList(),
    val error: String? = null
)