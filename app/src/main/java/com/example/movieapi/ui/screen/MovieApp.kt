package com.example.movieapi.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun MovieApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { NavigationBar(navController) } // Material 3 NavigationBar
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home", // İlk ekran
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { MovieListScreen(navController) } // Ana Sayfa
            composable("favorites") { FavoritesScreen(navController) } // Favoriler
            composable("cart") { CartScreen(navController) } // Sepet
            composable("movieDetail/{movieId}") { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
                if (movieId != null) {
                    MovieDetailScreen(movieId = movieId)
                } else {
                    Text(text = "Invalid movie ID", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun NavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, "home"),
        BottomNavItem("Favorites", Icons.Default.Favorite, "favorites"),
        BottomNavItem("Cart", Icons.Default.ShoppingCart, "cart")
    )

    NavigationBar(
        containerColor = Color(0xFFE5EBF0),
        contentColor = Color.White
    ) {
        val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(text = item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFF89E1F0) // Seçili öğe arka plan rengi
                )
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)
