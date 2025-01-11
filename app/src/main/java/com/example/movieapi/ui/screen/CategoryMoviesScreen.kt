package com.example.movieapi.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.movieapi.R
import com.example.movieapi.viewmodel.MovieViewModel

@Composable
fun CategoryMoviesScreen(navController: NavController, category: String, viewModel: MovieViewModel) {
    val allMovies = viewModel.getMoviesByCategory(category) // Kategoriye özel tüm filmler
    var filteredMovies by remember { mutableStateOf(allMovies) } // Filtrelenmiş liste

    var isSortDialogVisible by remember { mutableStateOf(false) }
    var isFilterDropdownVisible by remember { mutableStateOf(false) }
    var selectedDirector by remember { mutableStateOf<String?>(null) } // Seçili yönetmen

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Kategori: $category",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Sırala ve Filtrele Butonları
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF2F2F2))
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sırala Butonu
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { isSortDialogVisible = true }, // Dialogu aç
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.sort),
                    contentDescription = "Sırala",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sırala",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // Separator Çizgi
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(Color.Gray)
            )

            // Filtrele Butonu
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { isFilterDropdownVisible = true },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.filter),
                    contentDescription = "Filtrele",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Filtrele",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            if (isFilterDropdownVisible) {
                DirectorFilterDropdown(viewModel = viewModel) { director ->
                    selectedDirector = director
                    isFilterDropdownVisible = false

                    // Yönetmene göre filtreleme
                    filteredMovies = if (director.isNullOrEmpty()) {
                        allMovies // Tüm filmler
                    } else {
                        allMovies.filter { it.director == director }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Film listesi
        if (filteredMovies.isEmpty()) {
            Text(text = "Bu kategoriye ait film bulunamadı.")
        } else {
            LazyColumn {
                items(filteredMovies.chunked(2)) { rowMovies ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        rowMovies.forEach { movie ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                            ) {
                                MovieItem(
                                    movie = movie,
                                    navController = navController,
                                    viewModel = viewModel
                                )
                            }
                        }
                        if (rowMovies.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Sıralama seçeneklerini gösteren dialog
        if (isSortDialogVisible) {
            SortOptionsDialog(
                onDismiss = { isSortDialogVisible = false },
                onOptionSelected = { option ->
                    filteredMovies = when (option) {
                        "Fiyat Artan" -> filteredMovies.sortedBy { it.price }
                        "Fiyat Azalan" -> filteredMovies.sortedByDescending { it.price }
                        "Puana Göre" -> filteredMovies.sortedByDescending { it.rating }
                        else -> filteredMovies
                    }
                    isSortDialogVisible = false
                }
            )
        }
    }
}


@Composable
fun DirectorFilterDropdown(viewModel: MovieViewModel, onDirectorSelected: (String?) -> Unit) {
    val movies = viewModel.movies.collectAsState().value
    val directors = movies.mapNotNull { it.director }.distinct().sorted()

    Box {
        DropdownMenu(
            expanded = true,
            onDismissRequest = { onDirectorSelected(null) }
        ) {
            DropdownMenuItem(
                text = { Text(text = "Tüm Yönetmenler") },
                onClick = { onDirectorSelected(null) }
            )
            directors.forEach { director ->
                DropdownMenuItem(
                    text = { Text(text = director) },
                    onClick = { onDirectorSelected(director) }
                )
            }
        }
    }
}
