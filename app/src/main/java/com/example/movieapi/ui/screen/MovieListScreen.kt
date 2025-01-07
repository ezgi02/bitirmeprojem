package com.example.movieapi.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.movieapi.viewmodel.MovieViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.movieapi.data.Movie

@Composable
fun MovieListScreen(navController: NavController, viewModel: MovieViewModel = viewModel()) {
    val movies = viewModel.movies.collectAsState(initial = emptyList())

    if (movies.value.isEmpty()) {
        Text(text = "No movies available", modifier = Modifier.padding(16.dp))
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(movies.value) { _, movie ->
                MovieItem(movie=movie,navController=navController)  // Her film için MovieItem fonksiyonunu çağırıyoruz
            }
        }
    }

}
/*@Composable
fun MovieItem(movie: Movie) {
    // Card içine film ismi ve fiyatını yazdırıyoruz
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Film Adı: ${movie.name}", modifier = Modifier.padding(bottom = 4.dp))
            Text(text = "Fiyat: ${movie.price} TL")
        }
    }
}*/
@Composable
fun MovieItem(movie: Movie, navController: NavController) {
    // Temel URL'yi tanımlayın
    val baseUrl = "http://kasimadalan.pe.hu/movies/images/"
    val imageUrl = baseUrl + movie.image // movie.image, film resminin ismi

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                Log.d("MovieItem", "Navigating to movieDetail/${movie.id}")
                navController.navigate("movieDetail/${movie.id}")
            }, // Tıklanabilir yap

    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            val painter = rememberImagePainter(imageUrl)

            Image(
                painter = painter,
                contentDescription = "Movie Image",
                modifier = Modifier
                    .size(120.dp)
                    .padding(end = 16.dp)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = movie.name, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Price: \$${movie.price}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }

        }
    }
}
