package com.kienvo.cinetrack.presentation.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.cinetrack.data.repository.MovieRepositoryImpl
import com.kienvo.cinetrack.domain.model.Movie
import com.kienvo.cinetrack.domain.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// DetailViewModel: Chịu trách nhiệm lấy dữ liệu chi tiết của 1 bộ phim và cập nhật UI.
class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository =
        MovieRepositoryImpl(application.applicationContext)

    // Khởi tạo trạng thái ban đầu của màn hình Chi Tiết (Loading = false, dữ liệu trống).
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    // Hàm gọi lấy dữ liệu ngay khi người dùng bấm vào 1 phim.
    fun loadDetail(movieId: Int) {
        viewModelScope.launch {
            // Bước 1: Hiển thị bộ quay loading.
            _uiState.update { it.copy(isLoading = true) }

            // Bước 2: Load movie detail từ API thông qua Repository.
            val result = repository.getMovieDetail(movieId)
            val movie = result.getOrNull()

            // Bước 3: Nếu gọi API thành công (movie != null), ta cần kiểm tra xen bộ phim này đang có trong cơ sở dữ liệu nội tại (Watchlist / Đã xem) không.
            if (movie != null) {
                // Lắng nghe (Observe) danh sách phim đã xem.
                // Học thuật: Chạy một Coroutine độc lập (launch) lắng nghe Flow. Bất cứ khi nào danh sách update, biến `isWatched` sẽ tự tính lại.
                viewModelScope.launch {
                    repository.getWatched().collect { watchedMovies ->
                        val isWatched = watchedMovies.any { it.id == movieId }
                        _uiState.update { it.copy(isWatched = isWatched) }
                    }
                }

                // Lắng nghe trạng thái phim có trong danh sách Watchlist không.
                // Hàm collect sẽ "treo" (suspend) và nhận tín hiệu mới mỗi khi Database đổi, do đó UI "Lưu lại" sẽ nhấp nháy ngay tức khắc.
                repository.isInWatchlist(movieId).collect { inWatchlist ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            movie = movie,
                            isInWatchlist = inWatchlist,
                            error = null
                        )
                    }
                }
            } else {
                // Gọi API thất bại: Hiển thị lỗi, giấu loading.
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message
                    )
                }
            }
        }
    }

    // Toggle (Bật / Tắt) lưu phim vào Watchlist
    fun toggleWatchlist() {
        // Lấy phim hiện tại từ UI State. Nếu bị rỗng (?) thì return ngay để tránh lỗi.
        val movie = _uiState.value.movie ?: return
        viewModelScope.launch {
            if (_uiState.value.isInWatchlist) {
                repository.removeFromWatchlist(movie) // Đang lưu rồi thì ấn vào là Xoá
            } else {
                repository.addToWatchlist(movie)      // Chưa lưu thì ấn vào là Thêm
            }
        }
    }

    // Nút Bật/tắt trạng thái Đã xem
    fun toggleWatched() {
        val movie = _uiState.value.movie ?: return
        val currentlyWatched = _uiState.value.isWatched
        viewModelScope.launch {
            // Logic bổ sung thông minh: Nếu bạn đánh dấu "Đã xem", mà phim này còn chưa có tủ (chưa trong Watchlist),
            // nó phải đưa phim vào tủ trước rồi mới đổi thuộc tính isWatched, tránh lỗi cập nhật SQL vô hiệu.
            if (!_uiState.value.isInWatchlist) {
                repository.addToWatchlist(movie)
            }
            repository.markAsWatched(movie.id, !currentlyWatched)

            // Ép UI State cập nhật "Đã lưu" cờ `isInWatchlist = true` vì ta vừa add nó vào DB ngay lúc nãy nếu nó chưa tồn tại.
            _uiState.update { it.copy(isWatched = !currentlyWatched, isInWatchlist = true) }
        }
    }
}

// UI State cho màn hình Detail. Đại diện cho mọi thứ có thể hiển thị trên màn hình hiện tại.
data class DetailUiState(
    val isLoading: Boolean = false,
    val movie: Movie? = null,
    val isInWatchlist: Boolean = false,
    val isWatched: Boolean = false,
    val error: String? = null
)