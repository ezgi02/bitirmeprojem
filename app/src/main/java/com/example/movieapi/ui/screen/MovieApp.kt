package com.example.movieapi.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.movieapi.viewmodel.MovieViewModel
import com.example.movieapi.R
import com.example.movieapi.ui.theme.IndicatorColor
import com.example.movieapi.ui.theme.SelectedIconColor
import com.example.movieapi.ui.theme.SelectedTextColor
import com.example.movieapi.ui.theme.UnselectedIconColor
import com.example.movieapi.ui.theme.UnselectedTextColor

@Composable
fun MovieApp() {
    val navController = rememberNavController()
    val sharedViewModel: MovieViewModel = viewModel()

    // Onboarding durumu ve kullanıcı adı için state
    var isOnboardingFinished by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }

    if (!isOnboardingFinished) {
        // Onboarding ekranını göster
        OnboardingScreen { name ->
            userName = name
            isOnboardingFinished = true
        }
    } else {
        // Ana uygulama akışı
        Scaffold(
            bottomBar = { NavigationBar(navController) } // Material 3 NavigationBar
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home", // İlk ekran
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") { MovieListScreen(navController, viewModel = sharedViewModel) } // Ana Sayfa
                composable("favorites") { FavoritesScreen(navController, viewModel = sharedViewModel) } // Favoriler
                composable("categories") { CategoryListScreen(navController = navController) }
                composable("categoryMovies/{category}") { backStackEntry ->
                    val category = backStackEntry.arguments?.getString("category")
                    if (category != null) {
                        CategoryMoviesScreen(navController, category, viewModel = sharedViewModel)
                    } else {
                        Text(text = "Invalid category", modifier = Modifier.padding(16.dp))
                    }
                }
                composable("cart") { CartScreen(navController,viewModel = sharedViewModel) } // Sepet
                composable("movieDetail/{movieId}") { backStackEntry ->
                    val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
                    if (movieId != null) {
                        MovieDetailScreen(movieId = movieId,viewModel = sharedViewModel)
                    } else {
                        Text(text = "Invalid movie ID", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Anasayfa", Icons.Default.Home, "home"),
        BottomNavItem("Kategoriler", R.drawable.grid, "categories"),
        BottomNavItem("Favoriler", Icons.Default.Favorite, "favorites"),
        BottomNavItem("Sepet", Icons.Default.ShoppingCart, "cart")
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF6200EE)
    ) {
        val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    when (item.icon) {
                        is ImageVector -> Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp) // İkon boyutunu ayarlamak için
                        )
                        is Int -> Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp) // Drawable boyutunu ayarlamak için
                        )
                        else -> throw IllegalArgumentException("Unsupported icon type")
                    }
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
                    selectedIconColor = SelectedIconColor, // Seçili ikon ve yazı rengi
                    unselectedIconColor = UnselectedIconColor, // Seçili olmayan ikon rengi
                    selectedTextColor = SelectedTextColor, // Seçili yazı rengi
                    unselectedTextColor = UnselectedTextColor, // Seçili olmayan yazı rengi
                    indicatorColor = IndicatorColor // Seçili öğe arka plan rengi
                )
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: Any,
    val route: String
)