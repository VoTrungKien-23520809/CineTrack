package com.kienvo.cinetrack.presentation.navigation

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.cinetrack.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val isLoggedIn: Boolean = authRepository.isLoggedIn()

    // ── Dark theme ──────────────────────────────────────────────────────────
    private val darkThemeKey = booleanPreferencesKey("dark_theme")

    val isDarkTheme: StateFlow<Boolean> = dataStore.data
        .map { prefs -> prefs[darkThemeKey] ?: true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun toggleTheme() {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[darkThemeKey] = !(prefs[darkThemeKey] ?: true)
            }
        }
    }

    // ── Language ────────────────────────────────────────────────────────────
    private fun userLangKey(uid: String?) =
        stringPreferencesKey(if (uid != null) "language_$uid" else "language_guest")

    /** Mã ngôn ngữ của user đang đăng nhập: "en" | "vi" | "fr". Mặc định "vi". */
    val languageCode: StateFlow<String> = authRepository.currentUserFlow
        .flatMapLatest { user ->
            dataStore.data.map { prefs -> prefs[userLangKey(user?.uid)] ?: "vi" }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "vi")

    init {
        // Mỗi khi auth state thay đổi (login / logout / khởi động),
        // đọc ngôn ngữ đã lưu của user đó và áp dụng ngay.
        viewModelScope.launch {
            authRepository.currentUserFlow.collect { user ->
                val code = dataStore.data.first()[userLangKey(user?.uid)] ?: "vi"
                withContext(Dispatchers.Main) {
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(code)
                    )
                }
            }
        }
    }

    fun setLanguage(code: String) {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUser()?.uid
            dataStore.edit { prefs -> prefs[userLangKey(uid)] = code }
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
    }
}