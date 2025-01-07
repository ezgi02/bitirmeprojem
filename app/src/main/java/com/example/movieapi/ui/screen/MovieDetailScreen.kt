package com.example.movieapi.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.movieapi.viewmodel.MovieViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun MovieDetailScreen(movieId: Int, viewModel: MovieViewModel = viewModel()) {
    val movies = viewModel.movies.collectAsState(initial = emptyList())

    if (movies.value.isEmpty()) {
        // Yüklenme durumu göster
        Text(
            text = "Loading movie details...",
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        )
    } else {
        val movie = movies.value.firstOrNull { it.id == movieId }
        if (movie == null) {
            Text(
                text = "Movie not found",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        } else {
            //var quantity= remember { mutableStateOf(1) }
            var quantity by remember { mutableStateOf(1) }
            val userName="ezgi"
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF87CEEB)) // Açık mavi arka plan
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Film Resmi ve İsmi
                val imageUrl = "http://kasimadalan.pe.hu/movies/images/${movie.image}"
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = rememberImagePainter(imageUrl),
                            contentDescription = "Movie Image",
                            modifier = Modifier
                                .size(200.dp)
                                .padding(8.dp)
                        )
                        Text(
                            text = movie.name,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rating ve Yıl
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Rating: ${movie.rating}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    Text(
                        text = "Year: ${movie.year}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Açıklama
                Text(
                    text = movie.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Justify
                )

                // Fiyat ve Miktar Seçimi
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Price: ${movie.price} ₺",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    Text(
                        text = "Total: ${movie.price * quantity} ₺",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Miktar Arttırma/Azaltma
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Azaltma Butonu
                    Button(onClick = { if (quantity > 1) quantity-- }) {
                        Text(text = "-")
                    }

                    // Miktar
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )

                    // Arttırma Butonu
                    Button(onClick = { quantity++ }) {
                        Text(text = "+")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sepete Ekle Butonu
                Button(
                    onClick = {
                        viewModel.addToCart(movie, userName, quantity) // Sepete ekle
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E90FF)) // Mavi renk
                ) {
                    Text(
                        text = "Sepete Ekle",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
        }
            }
}


