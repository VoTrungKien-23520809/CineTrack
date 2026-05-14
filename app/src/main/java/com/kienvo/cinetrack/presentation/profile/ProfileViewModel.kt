package com.kienvo.cinetrack.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.kienvo.cinetrack.data.repository.AuthRepositoryImpl
import com.kienvo.cinetrack.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val authRepository: AuthRepository = AuthRepositoryImpl()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(user = authRepository.getCurrentUser()) }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }
}

data class ProfileUiState(
    val user: FirebaseUser? = null,
    val isLoggedOut: Boolean = false
)