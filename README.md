# 🎬 CineTrack

An Android app help you track movies you want to watch. Browse popular films, search for titles, and save them to your personal watchlist. Syncs across devices with Firebase.

## Tech Stack
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM + Clean Architecture (3 layers)
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

## Setup
1. Clone repo
2. Add `TMDB_API_KEY` vào `local.properties`
3. Add `google-services.json` into folder `app/`
4. Run!