package com.example.movieapi.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapi.data.CartItem
import com.example.movieapi.data.Movie
import com.example.movieapi.data.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart

    init {
        fetchMovies()
    }

    private fun fetchMovies() {
        viewModelScope.launch {
            try {
                val movieResponse = RetrofitInstance.api.getAllMovies()
                val movieList = movieResponse.movies  // movies listesine erişim
                Log.d("MovieFetch", "Fetched ${movieList.size} movies")
                _movies.value = movieList
            } catch (e: Exception) {
                Log.e("MovieFetch", "Error fetching movies", e)
            }
        }
    }
    fun getMovieById(id: Int): Movie? {
        if (_movies.value.isEmpty()) {
            Log.e("MovieViewModel", "Movies list is empty while fetching movie ID: ${id}")
            return null
        }
        return _movies.value.firstOrNull { it.id == id }
    }
    //Seğpetteki filmleri getirme
    fun fetchCart(userName: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getMovieCart(userName)
                _cart.value = response.movie_cart
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error fetching cart", e)
            }
        }
    }
    //Sepete film ekleme
    fun addToCart(movie: Movie, userName: String, orderAmount: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.insertMovieToCart(
                    name = movie.name,
                    image = movie.image,
                    price = movie.price,
                    category = movie.category,
                    rating = movie.rating,
                    year = movie.year,
                    director = movie.director,
                    description = movie.description,
                    orderAmount = orderAmount,
                    userName = userName
                )
                if (response.success == 1) {
                    fetchCart(userName) // Sepeti güncelle
                } else {
                    Log.e("CartViewModel", "Failed to add movie: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error adding to cart", e)
            }
        }
    }
}