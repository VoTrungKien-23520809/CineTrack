package com.kienvo.cinetrack.presentation.navigation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.cinetrack.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val isLoggedIn: Boolean = authRepository.isLoggedIn()

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
}