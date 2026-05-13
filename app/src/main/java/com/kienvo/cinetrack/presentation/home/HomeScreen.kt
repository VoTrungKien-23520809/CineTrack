package com.kienvo.cinetrack.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kienvo.cinetrack.domain.model.Movie

// Composable function: Hàm vẽ giao diện.
// Học thuật: Jetpack Compose sử dụng mô hình Declarative UI (Giao diện khai báo), giao diện được vẽ dựa trên trạng thái (State) hiện tại.
// Đời thường: Đây là bức tranh được vẽ tự động mỗi khi ông thư ký (ViewModel) thay đổi dữ liệu trên bàn (uiState).
@Composable
fun HomeScreen(
    // Lambda function: Callback (Hàm gọi lại) truyền từ ngoài vào để xử lý sự kiện click.
    onMovieClick: (Int) -> Unit,
    // Khởi tạo và ghi nhớ ViewModel trong vòng đời (Lifecycle) của Compose.
    viewModel: HomeViewModel = viewModel()
) {
    // Quan sát state (trạng thái) từ ViewModel.
    // Lifecycle-aware: Nó tự động ngừng theo dõi khi app bị ẩn xuống nền để tiết kiệm pin.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // State tĩnh lưu tab đang chọn (0 = Phổ biến, 1 = Top rated).
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Phổ biến", "Top rated")

    // Column: Sắp xếp các thành phần từ trên xuống dưới (giống LinearLayout vertical).
    Column {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        // Khối when rẽ nhánh dựa trên trạng thái của UiState
        when {
            // Khi đang tải: Hiển thị vòng xoay ở giữa màn hình.
            uiState.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            // Khi có lỗi: Gọi giao diện lỗi (ErrorView).
            uiState.error != null -> ErrorView(uiState.error!!) { viewModel.loadMovies() }
            // Khi thành công: Vẽ lưới danh sách phim.
            else -> {
                val movies = if (selectedTab == 0) uiState.popularMovies else uiState.topRatedMovies
                MovieGrid(movies = movies, onMovieClick = onMovieClick)
            }
        }
    }
}

@Composable
fun ErrorView(x0: String, content: @Composable () -> Unit) {
    TODO("Not yet implemented")
}

// LazyVerticalGrid: Lưới danh sách cuộn mượt (tương tự như RecyclerView với GridLayoutManager).
// Chữ "Lazy" nghĩa là nó chỉ tải và vẽ những mục nào đang hiển thị trên màn hình chứ không vẽ hết hàng ngàn bộ phim cùng lúc (tiết kiệm bộ nhớ).
@Composable
fun MovieGrid(movies: List<Movie>, onMovieClick: (Int) -> Unit) {
    LazyVerticalGrid(
        // Chia làm 2 cột
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(movies, key = { it.id }) { movie ->
            MovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
        }
    }
}

// Card đại diện cho từng con Phim trong lưới danh sách.
@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // AsyncImage: Tự động tải ảnh từ mạng qua thư viện Coil, có tích hợp sẵn cache ảnh.
            AsyncImage(
                model = movie.fullPosterUrl(),
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    // Tỉ lệ khung hình chuẩn của Poster phim là 2:3
                    .aspectRatio(2f / 3f),
                contentScale = ContentScale.Crop // Cắt cúp ảnh cho vừa vặn khung hình
            )
            Column(Modifier.padding(8.dp)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "⭐ ${movie.formattedRating()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}