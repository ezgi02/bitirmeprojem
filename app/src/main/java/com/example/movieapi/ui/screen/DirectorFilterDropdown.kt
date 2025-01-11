package com.example.movieapi.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.movieapi.viewmodel.MovieViewModel


@Composable
fun DirectorFilterDropdown(viewModel: MovieViewModel) {
    val movies = viewModel.movies.collectAsState().value
    val directors = movies.mapNotNull { it.director }.distinct().sorted()

    var expanded by remember { mutableStateOf(false) }
    var selectedDirector by remember { mutableStateOf<String?>(null) }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(text = selectedDirector ?: "Tüm Yönetmenler")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Tüm Yönetmenler seçeneği
            DropdownMenuItem(
                text = { Text(text = "Tüm Yönetmenler") },
                onClick = {  selectedDirector = null
                    viewModel.updateFilterDirector(null)
                    expanded = false }

            )

            // Yönetmen seçenekleri
            directors.forEach { director ->
                DropdownMenuItem(text = {  Text(text = director) }, onClick = { selectedDirector = director
                    viewModel.updateFilterDirector(director)
                    expanded = false })
            }

        }
    }
}
