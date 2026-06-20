package com.kienvo.cinetrack.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.res.stringResource
import com.kienvo.cinetrack.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.kienvo.cinetrack.presentation.detail.DetailScreen
import com.kienvo.cinetrack.presentation.home.HomeScreen
import com.kienvo.cinetrack.presentation.login.LoginScreen
import com.kienvo.cinetrack.presentation.profile.ProfileScreen
import com.kienvo.cinetrack.presentation.search.SearchScreen
import com.kienvo.cinetrack.presentation.watchlist.WatchlistScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel = hiltViewModel()
) {
    val startDestination = if (appViewModel.isLoggedIn) "home" else "login"
    val isDarkTheme by appViewModel.isDarkTheme.collectAsStateWithLifecycle()
    val currentLanguage by appViewModel.languageCode.collectAsStateWithLifecycle()

    val currentRoute by navController.currentBackStackEntryAsState()
    val showBottomBar = currentRoute?.destination?.route in
            listOf("home", "search", "watchlist", "profile")

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute?.destination?.route == "home",
                        onClick = {
                            navController.navigate("home") {
                                launchSingleTop = true
                                popUpTo("home")
                            }
                        },
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text(stringResource(R.string.nav_explore)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute?.destination?.route == "search",
                        onClick = {
                            navController.navigate("search") {
                                launchSingleTop = true
                                popUpTo("home")
                            }
                        },
                        icon = { Icon(Icons.Default.Search, null) },
                        label = { Text(stringResource(R.string.nav_search)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute?.destination?.route == "watchlist",
                        onClick = {
                            navController.navigate("watchlist") {
                                launchSingleTop = true
                                popUpTo("home")
                            }
                        },
                        icon = { Icon(Icons.Default.Bookmarks, null) },
                        label = { Text(stringResource(R.string.nav_watchlist)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute?.destination?.route == "profile",
                        onClick = {
                            navController.navigate("profile") {
                                launchSingleTop = true
                                popUpTo("home")
                            }
                        },
                        icon = { Icon(Icons.Default.Person, null) },
                        label = { Text(stringResource(R.string.nav_profile)) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            // Chỉ apply bottom padding (navigation bar) — các màn hình tự handle top inset
            modifier = Modifier.padding(
                bottom = padding.calculateBottomPadding()
            )
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
            composable("home") {
                HomeScreen(
                    onMovieClick = { navController.navigate("detail/$it") },
                    onProfileClick = {
                        navController.navigate("profile") {
                            launchSingleTop = true
                            popUpTo("home")
                        }
                    }
                )
            }
            composable("watchlist") {
                WatchlistScreen(onMovieClick = { navController.navigate("detail/$it") })
            }
            composable("detail/{movieId}") { backStack ->
                val movieId = backStack.arguments?.getString("movieId")?.toIntOrNull()
                    ?: return@composable
                DetailScreen(
                    movieId = movieId,
                    onBack = { navController.popBackStack() },
                    onMovieClick = { navController.navigate("detail/$it") }
                )
            }
            composable("profile") {
                ProfileScreen(
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    isDarkTheme     = isDarkTheme,
                    onToggleTheme   = { appViewModel.toggleTheme() },
                    currentLanguage = currentLanguage,
                    onLanguageChange = { appViewModel.setLanguage(it) }
                )
            }
            composable("search") {
                SearchScreen(onMovieClick = { navController.navigate("detail/$it") })
            }
        }
    }
}