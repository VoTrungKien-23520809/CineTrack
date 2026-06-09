package com.kienvo.cinetrack.presentation.watchlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.cinetrack.data.repository.MovieRepositoryImpl
import com.kienvo.cinetrack.domain.model.Movie
import com.kienvo.cinetrack.domain.repository.MovieRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WatchlistViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository =
        MovieRepositoryImpl(application.applicationContext)

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