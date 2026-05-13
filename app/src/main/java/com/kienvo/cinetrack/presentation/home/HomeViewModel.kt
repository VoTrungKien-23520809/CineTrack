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

// ViewModel: Nơi chịu trách nhiệm quản lý dữ liệu cho UI và xử lý các hành động từ người dùng.
// Học thuật: Triển khai mô hình MVVM (Model - View - ViewModel). ViewModel có lifecycle (vòng đời) dài hơn View (Ví dụ: xoay màn hình không bị mất data).
// Đời thường: Đây là gã "thư ký" phiên dịch yêu cầu của "ông sếp màn hình" (UI) xuống kho (Repository) lấy tài liệu, rồi sắp xếp lên bàn (StateFlow) cho sếp đọc.
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // Khởi tạo Repository bằng ApplicationContext.
    private val repository: MovieRepository =
        MovieRepositoryImpl(application.applicationContext)

    // _uiState lưu trạng thái hiện tại. Mutable -> Có thể thay đổi được. Đây là biến private chỉ nội bộ thư ký mới được sửa.
    private val _uiState = MutableStateFlow(HomeUiState())
    // uiState công khai ra ngoài dưới dạng Immutable (chỉ đọc) để màn hình (Screen/Compose) theo dõi thay đổi.
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Khối init tự động chạy ngay khi thư ký được sinh ra (App vừa vào màn hình Home).
    init {
        loadMovies()
    }

    // Hàm gọi mạng để lấy danh sách phim
    fun loadMovies() {
        // viewModelScope.launch tự động giải phóng các tác vụ mạng nền khi thoát khỏi màn hình (ngăn sập app rò rỉ bộ nhớ - memory leak).
        viewModelScope.launch {
            // Bước 1: Gán trạng thái loading để màn hình hiện vòng xoay quay quay. (Tạo 1 bản sao thông qua hàm .copy())
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Bước 2: Lần lượt gọi điện cho người đi lấy dữ liệu từ mạng.
            val popular = repository.getPopularMovies()
            val topRated = repository.getTopRatedMovies()

            // Bước 3: Cập nhật lại UI. Tắt vòng xoay, gán các danh sách phim nếu thành công, báo lỗi nếu thất bại.
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

// Data class chứa trọn vẹn tất cả dữ liệu có thể hiện ra trên màn hình Home.
// Học thuật: MVI / UDF (Unidirectional Data Flow) pattern. UI chỉ là hình bóng phản chiếu của UiState này. Điểm mạnh là cực kỳ dễ test logic.
// Đời thường: Một bức tranh toàn cảnh (snapshot) nói rõ hiện tại màn hình đang diễn ra cái gì (Có đang tải không? Bị lỗi không? Danh sách mảng nào?).
data class HomeUiState(
    val isLoading: Boolean = false,
    val popularMovies: List<Movie> = emptyList(),
    val topRatedMovies: List<Movie> = emptyList(),
    val error: String? = null
)