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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0) // 0 = muốn xem, 1 = đã xem
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    val wantToWatch: StateFlow<List<Movie>> = repository.getWantToWatch()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Danh sách "Đã xem" (Tương tự như wantToWatch)
    val watched: StateFlow<List<Movie>> = repository.getWatched()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Hành động người dùng chuyển Tab
    fun selectTab(index: Int) { _selectedTab.value = index }

    // Xoá phim khỏi mọi danh sách
    fun removeFromWatchlist(movie: Movie) {
        viewModelScope.launch { repository.removeFromWatchlist(movie) }
    }
}
