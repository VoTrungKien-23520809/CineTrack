package com.kienvo.cinetrack.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.kienvo.cinetrack.presentation.detail.DetailScreen
import com.kienvo.cinetrack.presentation.home.HomeScreen
import com.kienvo.cinetrack.presentation.login.LoginScreen
import com.kienvo.cinetrack.presentation.profile.ProfileScreen
import com.kienvo.cinetrack.presentation.watchlist.WatchlistScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
    val startDestination = if (isLoggedIn) "home" else "login"

    val currentRoute by navController.currentBackStackEntryAsState()
    val showBottomBar = currentRoute?.destination?.route in listOf("home", "watchlist", "profile")

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
                        label = { Text("Khám phá") }
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
                        label = { Text("Watchlist") }
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
                        label = { Text("Hồ sơ") }
                    )

                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
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
                HomeScreen(onMovieClick = { navController.navigate("detail/$it") })
            }
            composable("watchlist") {
                WatchlistScreen(onMovieClick = { navController.navigate("detail/$it") })
            }
            composable("detail/{movieId}") { backStack ->
                val movieId = backStack.arguments?.getString("movieId")?.toInt()
                    ?: return@composable
                DetailScreen(
                    movieId = movieId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("profile") {
                ProfileScreen(
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true } // clear toàn bộ backstack
                        }
                    }
                )
            }
        }
    }
}