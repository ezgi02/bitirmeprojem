package com.example.movieapi.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.movieapi.data.Movie
import com.example.movieapi.viewmodel.MovieViewModel
//tehlike
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.movieapi.R
import com.example.movieapi.ui.theme.Backgroundcolor
import com.example.movieapi.ui.theme.TitleColor

//Log.d("ViewModelInstance", "MovieListScreen ViewModel: $viewModel")

@Composable
fun MovieListScreen(navController: NavController, viewModel: MovieViewModel = viewModel()) {
    //val movies = viewModel.movies.collectAsState(initial = emptyList())
    val movies = viewModel.filteredMovies.collectAsState(initial = emptyList()) // Filtrelenmiş film listesi


    val searchQuery = viewModel.searchQuery.collectAsState()

    // Sort sheet görünürlüğü için state
    var isSortSheetVisible by remember { mutableStateOf(false) }

    // Dropdown görünürlük durumu
    var isFilterDropdownVisible by remember { mutableStateOf(false) }

    // Seçili yönetmen
    var selectedDirector by remember { mutableStateOf<String?>(null) }


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        // Sayfa Başlığı
        Text(
            text = "Film Sepeti",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp, color = TitleColor),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Arama Çubuğu
        TextField(
            value =  searchQuery.value, // Arama metni
            onValueChange = { viewModel.updateSearchQuery(it)  },
            placeholder = { Text("Film ara...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = Color.Gray // İkon rengi
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = TitleColor, // Odaklanıldığında mor çerçeve
                unfocusedBorderColor = Color.Gray, // Odaklanılmadığında gri çerçeve
                cursorColor = TitleColor // İmleç rengi
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(Color.White, shape = RoundedCornerShape(12.dp)) // Köşeleri yuvarlama
                .border(1.dp, Color.Gray, shape = RoundedCornerShape(12.dp)) // Çerçeve ekleme
        )

       // Sırala ve Filtrele Butonları
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Backgroundcolor) // Arka plan rengi
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Sırala Butonu
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable {  isSortSheetVisible = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.sort), // Sırala ikonu
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
// Yönetmen Filtresi
       //     DirectorFilterDropdown(viewModel = viewModel)
            // Filtrele Butonu
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable {  isFilterDropdownVisible = true },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.filter), // Filtrele ikonu
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
                DropdownMenu(
                    expanded = isFilterDropdownVisible,
                    onDismissRequest = { isFilterDropdownVisible = false },
                    modifier = Modifier.width(200.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text("Tüm Yönetmenler") },
                        onClick = {
                            viewModel.updateFilterDirector(null)
                            isFilterDropdownVisible = false
                        }
                    )
                    val directors =
                        viewModel.movies.collectAsState().value.mapNotNull { it.director }
                            .distinct()
                    directors.forEach { director ->
                        DropdownMenuItem(
                            text = { Text(director) },
                            onClick = {
                                viewModel.updateFilterDirector(director)
                                isFilterDropdownVisible = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(movies.value.chunked(2)) { rowMovies ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowMovies.forEach { movie ->
                        Box(
                            modifier = Modifier
                                .weight(1f, fill = true)
                                .padding(8.dp)
                        ) {
                            MovieItem(movie = movie, navController = navController, viewModel = viewModel)
                        }
                    }
                    // Eğer bir satırda tek bir film varsa, boş bir alan ekleyerek hizalamayı koruyun
                    if (rowMovies.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        if (isSortSheetVisible) {
            SortOptionsDialog(
                onDismiss = { isSortSheetVisible = false },
                onOptionSelected = { option ->
                    viewModel.sortMoviesBy(option) // Sıralama işlemi
                    isSortSheetVisible = false
                }
            )
        }
    }
}

@Composable
fun MovieItem(movie: Movie, navController: NavController,viewModel: MovieViewModel) {
    val isFavorite = viewModel.favorites.collectAsState().value.contains(movie)
    val userName=viewModel.userName.collectAsState().value
    val context = LocalContext.current // Context'i alın

    //val viewModel: MovieViewModel = viewModel()
    val baseUrl = "http://kasimadalan.pe.hu/movies/images/"
    val imageUrl = baseUrl + movie.image

    Card(
        modifier = Modifier
            .fillMaxWidth() // Kart genişliği (3 kart için uygun boyut)
            .padding(2.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(0.dp))// Çizgi ekleme
            .clickable {
                Log.d("MovieItem", "Navigating to movieDetail/${movie.id}")
                navController.navigate("movieDetail/${movie.id}")
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Arka plan rengini açık mavi yap
        ),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {

            Box(
                modifier = Modifier.fillMaxWidth() // Tüm genişliği kapla
            ) {
                // Film Posteri
                val painter = rememberImagePainter(imageUrl)
                Image(
                    painter = painter,
                    contentDescription = "Movie Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.75f) // En-boy oranını koru
                )

                // Favori İkonu
                IconButton(
                    onClick = { viewModel.toggleFavorite(movie) },
                    modifier = Modifier
                        .align(Alignment.TopEnd) // Sağ üst köşeye hizala
                        //.padding(top = 2.dp, end = 2.dp) // Daha fazla köşeye yaklaş
                        .offset(x = 17.dp, y = (-13).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = if (isFavorite) "Remove from Favorites" else "Add to Favorites",
                        tint = if (isFavorite) Color.Red else Color.Gray
                    )
                }
            }


            Spacer(modifier = Modifier.height(8.dp))

            // Film Adı
            Text(
                text = movie.name,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            // Film Fiyatı
            Text(
                text = "Fiyat: \$${movie.price}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            // Kullanıcı Puanı
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.star),// Yıldız ikonu
                    contentDescription = "Rating",
                    tint = Color.Yellow ,
                    modifier = Modifier.size(17.dp) // İkon boyutu

                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${movie.rating}", style = MaterialTheme.typography.bodySmall)
            }
            //Sepete Ekleme
            Button(
                onClick = {   if (userName.isNotEmpty()) {
                    viewModel.addToCart(movie, userName, 1) // Sepete ekleme
                    Toast.makeText(context, "Sepete eklendi", Toast.LENGTH_SHORT).show() // Toast göster
                } else {
                    Log.e("MovieItem", "User name is empty. Cannot add to cart.")
                }},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TitleColor, // Buton arka plan rengi
                    contentColor = Color.White // Buton yazı rengi
                ),
                shape =  RoundedCornerShape(0.dp) // Köşe yuvarlamasını kaldır
            ) {
                Text("Sepete Ekle", style = MaterialTheme.typography.bodyMedium)
            }
            }
        }
    }

@Composable
fun FilterDropdown(viewModel: MovieViewModel, onDirectorSelected: (String?) -> Unit) {
    val movies = viewModel.movies.collectAsState().value
    val directors = movies.mapNotNull { it.director }.distinct().sorted()

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
