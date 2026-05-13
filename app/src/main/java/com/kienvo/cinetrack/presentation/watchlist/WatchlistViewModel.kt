package com.kienvo.cinetrack.presentation.watchlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.cinetrack.data.repository.MovieRepositoryImpl
import com.kienvo.cinetrack.domain.model.Movie
import com.kienvo.cinetrack.domain.repository.MovieRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// WatchlistViewModel: Quản lý luồng dữ liệu cho màn hình danh sách Watchlist (Ghi nhớ / Đã xem).
// Học thuật: StateIn được sử dụng nhằm biến một cold Flow (chỉ chạy khi có người collect) thành một hot StateFlow (luôn giữ trạng thái cuối cùng trên memory).
// Đời thường: Ông thư ký này phụ trách một két sắt (Database) và chia làm 2 ngăn rõ rệt. Khi người dùng chọn tab nào thì mở ngăn đó.
class WatchlistViewModel(application: Application) : AndroidViewModel(application) {

    // Khởi tạo Repository bằng ApplicationContext.
    private val repository: MovieRepository =
        MovieRepositoryImpl(application.applicationContext)

    // State lưu giữ Tab hiện đang nằm (0 = "Muốn xem", 1 = "Đã xem")
    private val _selectedTab = MutableStateFlow(0) // 0 = muốn xem, 1 = đã xem
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Danh sách phim "Muốn xem". Lấy từ SQLite trả về liên tục (Auto-update).
    // Học thuật: SharingStarted.WhileSubscribed(5000) giúp dừng việc lấy dữ liệu ngầm từ Database nếu UI đã bị hủy hoặc ẩn đi hơn 5 giây. Tiết kiệm tài nguyên máy!
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