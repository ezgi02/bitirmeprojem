package com.example.movieapi.ui.screen

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import  com.example.movieapi.R
@Composable
fun MovieDetailScreen(movieId: Int, viewModel: MovieViewModel = viewModel()) {
    val movies = viewModel.movies.collectAsState(initial = emptyList())
    val context = LocalContext.current // Context'i alın

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
            var quantity by remember { mutableStateOf(1) }
            val userName = viewModel.userName.collectAsState().value
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White) // Arka planı beyaz yap
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
                    // Rating ve Yıldız
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(color = Color(0xFF6200EE), shape = RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Rating: ${movie.rating}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp)) // Metin ve ikon arasına boşluk
                        androidx.compose.material3.Icon(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.star), // Yıldız ikonu
                            contentDescription = "Star Icon",
                            tint = Color.Yellow, // İkon rengi
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(color = Color(0xFF6200EE), shape = RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ){
                        Text(
                            text = "Year: ${movie.year}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }

                }


                Spacer(modifier = Modifier.height(16.dp))

                // Açıklama
                Text(
                    text = movie.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Justify
                )
                Spacer(modifier = Modifier.height(8.dp)) // Açıklama ve yönetmen ismi arasında boşluk
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(color = Color(0xFF6200EE), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ){
                    Text(
                        text = "Yönetmen: ${movie.director}", // Yönetmen ismi
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fiyat ve Miktar Seçimi
               /* Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Price: ${movie.price} ₺",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                    Text(
                        text = "Total: ${movie.price * quantity} ₺",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                }*/

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
                    Button(
                        onClick = { if (quantity > 1) quantity-- },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)) // Renk değiştirildi
                    ) {
                        Text(text = "-")
                    }

                    // Miktar
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black
                    )

                    // Arttırma Butonu
                    Button(
                        onClick = { quantity++ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)) // Renk değiştirildi
                    ) {
                        Text(text = "+")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sepete Ekle Butonu
               /* Button(
                    onClick = {
                        viewModel.addToCart(movie, userName, quantity) // Sepete ekle
                        Toast.makeText(context, "Sepete eklendi", Toast.LENGTH_SHORT).show() // Toast göster
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)) // Renk değiştirildi
                ) {
                    Text(
                        text = "Sepete Ekle",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }*/
                // Fiyat, Toplam ve Sepete Ekle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Fiyat ve Toplam Bilgisi
                    Column {
                        Text(
                            text = "Price: ${movie.price} ₺",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // Fiyat ve toplam arasında boşluk
                        Text(
                            text = "Total: ${movie.price * quantity} ₺",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black
                        )
                    }

                    // Sepete Ekle Butonu
                    Button(
                        onClick = {
                            viewModel.addToCart(movie, userName, quantity) // Sepete ekle
                            Toast.makeText(context, "Sepete eklendi", Toast.LENGTH_SHORT).show() // Toast göster
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)) // Buton rengi
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
}
/*Text(
text = "Hoş geldiniz, $userName!",
style = MaterialTheme.typography.bodyLarge,
modifier = Modifier.padding(bottom = 16.dp)
)*/