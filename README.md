# 🎬 CineTrack

An Android app help you track movies you want to watch. Browse popular films, search for titles, and save them to your personal watchlist. Syncs across devices with Firebase.

## Tech Stack
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM + Clean Architecture (3 layers)
- **Dependency Injection:** Hilt (Dagger)
- **Database:** Room (offline) + Firestore (cloud sync)
- **Auth:** Firebase Auth (Google + Email/Password)
- **Networking:** Retrofit + OkHttp
- **Async:** Kotlin Coroutines + Flow
- **Image loading:** Coil

## Features
- 🔐 Logging in with Google or email/password
- 🎬 Browse popular movies from TMDB API
- 🔍 Search for movies by title
- 💾 Save movies to your watchlist - stored locally and synced to the Firestore cloud
- ✅ Mark movies as watched/unwatched
- 🌙 Dark theme

## Architecture

The app follows **Clean Architecture** with three layers, fully decoupled through **Hilt** dependency injection:

```
presentation/   →  UI (Compose screens) + ViewModels (@HiltViewModel)
        ↓ depends on
domain/         →  Pure Kotlin: models (Movie, User) + repository interfaces
        ↑ implemented by
data/           →  Repository implementations, Room, Retrofit, Firestore
```

- **presentation** depends only on **domain** abstractions — never on `data` directly.
- **domain** has no Android/Firebase dependencies (e.g. `FirebaseUser` is mapped to a pure `User` model).
- **data** implements the domain interfaces; Hilt binds them at runtime via `RepositoryModule`.

### Dependency Injection (Hilt)
All wiring lives in the `di/` package, installed in `SingletonComponent`:

| Module | Provides |
|--------|----------|
| `AppModule` | `FirebaseAuth`, `FirebaseFirestore` |
| `DatabaseModule` | `CineTrackDatabase`, `MovieDao` |
| `NetworkModule` | `OkHttpClient` (API-key interceptor + debug logging), `Retrofit`, `TmdbApiService` |
| `RepositoryModule` | Binds `AuthRepository`/`MovieRepository` to their implementations |

The TMDB API key is injected through an OkHttp interceptor from `BuildConfig`, so it never appears in source.

## Project Structure

```
app/src/main/java/com/kienvo/cinetrack/
├── CineTrackApplication.kt        # @HiltAndroidApp entry point
├── MainActivity.kt                # @AndroidEntryPoint host
├── di/                            # Hilt modules
├── domain/
│   ├── model/                     # Movie, User
│   └── repository/                # AuthRepository, MovieRepository (interfaces)
├── data/
│   ├── local/                     # Room: database, DAO, entities
│   ├── remote/                    # Retrofit API service + DTOs
│   └── repository/                # Repository implementations
└── presentation/
    ├── navigation/                # AppNavigation, AppViewModel
    ├── home/  detail/  search/    # Feature screens + ViewModels
    ├── watchlist/  login/  profile/
    └── components/                # Reusable UI (shimmer, ...)
```

## Setup
1. Clone repo
2. Add `TMDB_API_KEY` vào `local.properties`
3. Add `google-services.json` into folder `app/`
4. Run!

## Build

```bash
# Debug APK
./gradlew assembleDebug

# Install on a connected device/emulator
./gradlew installDebug
```

> **Note:** The local watchlist uses Room schema `version 3` with `fallbackToDestructiveMigration()`.
> Upgrading from an older schema wipes the **local** watchlist cache — data already synced to Firestore is restored on next load.