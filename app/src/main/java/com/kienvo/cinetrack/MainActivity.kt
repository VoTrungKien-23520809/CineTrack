package com.kienvo.cinetrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.kienvo.cinetrack.presentation.navigation.AppNavigation
import com.kienvo.cinetrack.presentation.navigation.AppViewModel
import com.kienvo.cinetrack.ui.theme.CineTrackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by appViewModel.isDarkTheme.collectAsStateWithLifecycle()
            CineTrackTheme(isDark = isDarkTheme) {
                AppNavigation(appViewModel = appViewModel)
            }
        }
    }
}