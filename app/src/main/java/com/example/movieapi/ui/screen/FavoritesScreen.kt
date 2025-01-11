package com.example.movieapi.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.movieapi.ui.theme.TitleColor
import com.example.movieapi.viewmodel.MovieViewModel

@Composable
fun FavoritesScreen(navController: NavController,viewModel: MovieViewModel){

    Log.d("ViewModelInstance", "FavoritesScreen ViewModel: $viewModel")

    // Favori filmleri State olarak topluyoruz.
    val favoriteMovies = viewModel.favorites.collectAsState(emptyList()).value
    Log.d("FavoritesScreen", "Collected favorites: $favoriteMovies")

    // Ana ekran düzeni
    Column(modifier = Modifier.fillMaxSize()) {
        // Başlık
        Text(
            text = "Favori Film Listesi",
            style = MaterialTheme.typography.headlineMedium,
            color = TitleColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
        // Eğer favoriler boşsa, kullanıcıya bilgi veren bir mesaj gösteriyoruz.
        if (favoriteMovies.isEmpty()) {
            Text(
                text = "No favorites yet.",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(favoriteMovies.chunked(2)) { rowMovies -> // Her satırda 2 film olacak şekilde gruplama
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp), // Her satır arasına dikey boşluk
                        horizontalArrangement = Arrangement.SpaceBetween // Kartlar arasında eşit boşluk
                    ) {
                        // Her filmi bir kutu içinde düzenliyoruz.
                        rowMovies.forEach { movie ->
                            Box(
                                modifier = Modifier
                                    .weight(1f) // Her kart eşit genişlik alır
                                    .padding(horizontal = 8.dp) // Her kartın yanlarına boşluk
                            ) {
                                MovieItem(movie = movie, navController = navController, viewModel = viewModel)
                            }
                        }
                        // Eğer bir satırda sadece bir öğe varsa, hizalamayı korumak için boş bir alan ekleyin
                        if (rowMovies.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

        }
    }

    }
