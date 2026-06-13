package com.kienvo.cinetrack.presentation.navigation

import androidx.lifecycle.ViewModel
import com.kienvo.cinetrack.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {

    val isLoggedIn: Boolean = authRepository.isLoggedIn()
}
