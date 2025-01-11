package com.example.movieapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.movieapi.data.datasource.MovieDataSource
import com.example.movieapi.data.repo.MovieRepository
import com.example.movieapi.retrofit.RetrofitInstance
import com.example.movieapi.ui.screen.MovieApp
import com.example.movieapi.ui.screen.MovieListScreen
import com.example.movieapi.ui.theme.MovieApiTheme
import com.example.movieapi.viewmodel.MovieViewModel

class MainActivity : ComponentActivity() {
    private lateinit var movieViewModel: MovieViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bağlantıları oluştur
        val movieApi = RetrofitInstance.api
        val dataSource = MovieDataSource(movieApi)
        val repository = MovieRepository(dataSource)
        val factory = MovieViewModelFactory(repository)

        // ViewModel'i oluştur
        movieViewModel = ViewModelProvider(this, factory).get(MovieViewModel::class.java)
        setContent {
            MovieApiTheme {
                // A surface container using the 'background' color from the theme
                MovieApp(viewModel = movieViewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MovieApiTheme {
        Greeting("Android")
    }
}