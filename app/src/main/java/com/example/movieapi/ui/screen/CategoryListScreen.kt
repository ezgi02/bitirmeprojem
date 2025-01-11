package com.example.movieapi.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movieapi.viewmodel.MovieViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movieapi.R


@Composable
fun CategoryListScreen(navController: NavController, viewModel: MovieViewModel = viewModel()) {
    val movies = viewModel.movies.collectAsState(initial = emptyList()).value

    // Kategorileri gruplandır
    val categories = movies.groupBy { it.category }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Kategoriler",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(categories.keys.toList()) { category ->
                CategoryItem(
                    category = category,
                    onClick = {
                        // Tıklanan kategoriye ait filmleri göster
                        navController.navigate("categoryMovies/$category")
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(category: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right), // Sağ ok ikonu
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}
