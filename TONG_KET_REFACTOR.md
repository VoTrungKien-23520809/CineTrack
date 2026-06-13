# Tổng kết Refactor CineTrack

Refactor toàn bộ project theo kết quả code review: thêm **Hilt DI**, chuẩn hoá **Clean Architecture**, sửa **Room bugs**, fix **coroutine leaks**, cải thiện **bảo mật API key**.

Thứ tự thực hiện: **Dependencies → Domain → Data → DI → Presentation**.

Trạng thái cuối cùng: ✅ `BUILD SUCCESSFUL`.

---

## Phase 1 — Build Configuration (Thêm Hilt)

| File | Thay đổi |
|------|----------|
| `gradle/libs.versions.toml` | Thêm version + library cho `hilt-android`, `hilt-compiler`, `hilt-navigation-compose` và plugin Hilt |
| `build.gradle.kts` (root) | Khai báo plugin `com.google.dagger.hilt.android` |
| `app/build.gradle.kts` | Apply plugin `hilt` + `ksp`; thêm dependencies Hilt; gộp duplicate Compose BOM; chuẩn hoá dùng `BuildConfig.TMDB_API_KEY` |

---

## Phase 2 — Domain Layer (Tách Firebase khỏi domain)

| File | Thay đổi |
|------|----------|
| `domain/model/User.kt` | **[MỚI]** Pure domain model: `uid`, `displayName`, `email`, `photoUrl`, `providerId` — không phụ thuộc `FirebaseUser` |
| `domain/repository/AuthRepository.kt` | Thay toàn bộ return type `FirebaseUser` → `User` |
| `domain/repository/MovieRepository.kt` | Thêm `suspend fun searchMovies(query: String): Result<List<Movie>>` |

---

## Phase 3 — Data Layer (Fix Room, API, Repositories)

| File | Thay đổi |
|------|----------|
| `data/local/entity/MovieEntity.kt` | Đổi sang **composite primary key** `["userId", "id"]`, bỏ `localId`, thêm `@Index` |
| `data/local/dao/MovieDao.kt` | Thay `@Delete` bằng `@Query("DELETE ... WHERE userId AND id")` (fix xoá nhầm bản ghi) |
| `data/local/CineTrackDatabase.kt` | Bỏ singleton companion object (Hilt quản lý), tăng `version = 3` |
| `data/remote/ApiKeyProvider.kt` | **[XOÁ]** — thay bằng `ApiKeyInterceptor` + `BuildConfig` |
| `data/remote/RetrofitInstance.kt` | **[XOÁ]** — thay bằng Hilt `NetworkModule` |
| `data/remote/TmdbApiService.kt` | Bỏ `@Query("api_key")` khỏi tất cả method — key được inject qua Interceptor |
| `data/repository/AuthRepositoryImpl.kt` | `@Inject constructor(auth)`; trả `User`; thêm extension `FirebaseUser.toDomain()` |
| `data/repository/MovieRepositoryImpl.kt` | `@Inject constructor(api, dao, firestoreRepo, auth)`; implement `searchMovies()`; `currentUserId` null-safe (không crash, trả về sớm) |
| `data/repository/FirestoreWatchlistRepository.kt` | `@Inject constructor(db, auth)`; thêm logging khi user null; xoá `observeWatchlist()` (dead code) |

---

## Phase 4 — DI Layer (Hilt Modules)

| File | Nội dung |
|------|----------|
| `CineTrackApplication.kt` | **[MỚI]** `@HiltAndroidApp` Application class |
| `di/AppModule.kt` | **[MỚI]** Provide `FirebaseAuth`, `FirebaseFirestore` (`@Singleton`) |
| `di/DatabaseModule.kt` | **[MỚI]** Provide `CineTrackDatabase` (+ `fallbackToDestructiveMigration`), `MovieDao` |
| `di/NetworkModule.kt` | **[MỚI]** Provide `OkHttpClient` (ApiKeyInterceptor + logging chỉ bật ở DEBUG), `Retrofit`, `TmdbApiService` |
| `di/RepositoryModule.kt` | **[MỚI]** `@Binds` `AuthRepository → AuthRepositoryImpl`, `MovieRepository → MovieRepositoryImpl` |

---

## Phase 5 — Presentation: ViewModels

Tất cả ViewModel chuyển sang `@HiltViewModel` + `@Inject constructor`, bỏ `AndroidViewModel`.

| File | Thay đổi |
|------|----------|
| `presentation/home/HomeViewModel.kt` | Inject `MovieRepository`; gộp error handling cho cả `popular` và `topRated` |
| `presentation/detail/DetailViewModel.kt` | Inject `MovieRepository`; **fix coroutine leak**: tách API call (one-shot) khỏi Flow collector; collector đặt trong `observeJob` có thể huỷ, cancel mỗi lần `loadDetail()` |
| `presentation/login/LoginViewModel.kt` | Inject `AuthRepository`; `LoginUiState.user` dùng `User` thay `FirebaseUser` |
| `presentation/search/SearchViewModel.kt` | Inject `MovieRepository`, dùng `repository.searchMovies()`; private `_query` + public `query: StateFlow`; hàm `onQueryChanged()` |
| `presentation/watchlist/WatchlistViewModel.kt` | Inject `MovieRepository`, plain `ViewModel` |
| `presentation/profile/ProfileViewModel.kt` | Inject `AuthRepository`; `ProfileUiState.user` dùng `User` |

---

## Phase 6 — Presentation: Screens & Navigation

Tất cả màn hình: `viewModel()` → `hiltViewModel()`.

| File | Thay đổi |
|------|----------|
| `presentation/navigation/AppViewModel.kt` | **[MỚI]** `@HiltViewModel` inject `AuthRepository` để xác định trạng thái đăng nhập |
| `presentation/navigation/AppNavigation.kt` | Dùng `AppViewModel` thay `FirebaseAuth.getInstance()`; `toInt()` → `toIntOrNull()`; `hiltViewModel()` cho mọi screen |
| `presentation/home/HomeScreen.kt` | `hiltViewModel()` |
| `presentation/detail/DetailScreen.kt` | `hiltViewModel()`; bỏ `uiState.movie!!` → dùng local `val movie` null-safe |
| `presentation/login/LoginScreen.kt` | `hiltViewModel()` |
| `presentation/search/SearchScreen.kt` | `hiltViewModel()`; gọi `viewModel.onQueryChanged()` thay vì set flow trực tiếp |
| `presentation/watchlist/WatchListScreen.kt` | `hiltViewModel()` |
| `presentation/profile/ProfileScreen.kt` | `hiltViewModel()`; `providerData.any{...}` → `providerId == "google.com"` (domain `User`) |
| `MainActivity.kt` | Thêm `@AndroidEntryPoint`, xoá unused imports |
| `AndroidManifest.xml` | Thêm `android:name=".CineTrackApplication"` |

---

## ⚠️ Lưu ý quan trọng

- **Mất dữ liệu watchlist cục bộ**: Room đổi composite primary key + `version = 3` + `fallbackToDestructiveMigration()` → dữ liệu Room hiện tại **bị xoá** khi user update. Bản sao trên Firestore **không** bị ảnh hưởng.
- **Cần test thủ công** luồng runtime (DI + auth chỉ kiểm chứng đầy đủ khi chạy app): đăng nhập → xem danh sách phim → thêm/xoá watchlist → tìm kiếm → hồ sơ → đăng xuất.

---

## Cách build kiểm tra

```bash
cd d:\CineTrack && .\gradlew assembleDebug
```
