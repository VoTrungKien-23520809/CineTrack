package com.kienvo.cinetrack.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.cinetrack.domain.model.User
import com.kienvo.cinetrack.domain.repository.AuthRepository
import com.kienvo.cinetrack.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    movieRepository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(user = authRepository.getCurrentUser()) }

        viewModelScope.launch {
            combine(
                movieRepository.getWantToWatch(),
                movieRepository.getWatched()
            ) { wantToWatch, watched ->
                wantToWatch.size to watched.size
            }.collect { (wantCount, watchedCount) ->
                _uiState.update {
                    it.copy(wantToWatchCount = wantCount, watchedCount = watchedCount)
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }
}

data class ProfileUiState(
    val user: User? = null,
    val wantToWatchCount: Int = 0,
    val watchedCount: Int = 0,
    val isLoggedOut: Boolean = false
)
