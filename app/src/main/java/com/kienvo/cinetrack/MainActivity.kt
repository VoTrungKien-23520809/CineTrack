package com.kienvo.cinetrack

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kienvo.cinetrack.presentation.navigation.AppNavigation
import com.kienvo.cinetrack.presentation.navigation.AppViewModel
import com.kienvo.cinetrack.ui.theme.CineTrackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()

    // Cần thiết cho API 24-32: ComponentActivity không tự apply locale từ AppCompatDelegate,
    // nên ta wrap context thủ công để stringResource() dùng đúng ngôn ngữ.
    override fun attachBaseContext(newBase: Context) {
        val localeList = AppCompatDelegate.getApplicationLocales()
        if (!localeList.isEmpty) {
            val config = Configuration(newBase.resources.configuration)
            ConfigurationCompat.setLocales(config, localeList)
            super.attachBaseContext(newBase.createConfigurationContext(config))
        } else {
            super.attachBaseContext(newBase)
        }
    }

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