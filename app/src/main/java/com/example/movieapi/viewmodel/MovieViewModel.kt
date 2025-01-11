package com.example.movieapi.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapi.data.entity.CartItem
import com.example.movieapi.data.entity.Movie
import com.example.movieapi.data.repo.MovieRepository
import com.example.movieapi.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart

    private val _searchQuery = MutableStateFlow("") // Arama metni
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filterDirector = MutableStateFlow<String?>(null) // Yönetmen filtresi
    val filterDirector: StateFlow<String?> = _filterDirector

    // Filtrelenmiş filmler
    val filteredMovies: StateFlow<List<Movie>> = combine(_movies, _searchQuery, _filterDirector) { movies, query, director ->
        var filteredList = movies

        // Arama metnine göre filtreleme
        if (query.isNotEmpty()) {
            filteredList = filteredList.filter { it.name.contains(query, ignoreCase = true) }
        }

        // Yönetmene göre filtreleme
        if (!director.isNullOrEmpty()) {
            filteredList = filteredList.filter { it.director.equals(director, ignoreCase = true) }
        }

        filteredList
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptyList())

    private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
    val favorites: StateFlow<List<Movie>> = _favorites

    fun updateUserName(name: String) {
        _userName.value = name
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFilterDirector(director: String?) {
        _filterDirector.value = director
    }

    init {
        fetchMovies()
    }

    // Tüm filmleri getir
    fun fetchMovies() {
        viewModelScope.launch {
            try {
                val movieList = repository.getAllMovies()
                _movies.value = movieList
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error fetching movies", e)
                _movies.value = emptyList()
            }
        }
    }

    // Sepetteki filmleri getir
    fun fetchCart(userName: String) {
        viewModelScope.launch {
            try {
                val response = repository.getMovieCart(userName)
                _cart.value = response.movie_cart ?: emptyList()
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error fetching cart", e)
                _cart.value = emptyList()
            }
        }
    }

    // Sepete film ekle
    fun addToCart(movie: Movie, userName: String, orderAmount: Int) {
        viewModelScope.launch {
            try {
                repository.insertMovieToCart(
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
                fetchCart(userName) // Sepeti güncelle
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error adding to cart", e)
            }
        }
    }

    // Sepetten film sil
    fun removeFromCart(cartId: Int, userName: String) {
        viewModelScope.launch {
            try {
                repository.deleteMovieFromCart(cartId, userName)
                fetchCart(userName) // Sepeti güncelle
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error removing from cart", e)
            }
        }
    }

    // Favorilere ekle veya çıkar
    fun toggleFavorite(movie: Movie) {
        val currentFavorites = _favorites.value.toMutableList()
        if (currentFavorites.contains(movie)) {
            currentFavorites.remove(movie) // Favoriden çıkar
        } else {
            if (currentFavorites.size < 5) { // Maksimum 5 favori sınırı
                currentFavorites.add(movie)
            }
        }
        _favorites.value = currentFavorites
        Log.d("MovieViewModel", "Current favorites: ${_favorites.value}")
    }

    // Filmin favori olup olmadığını kontrol et
    fun isFavorite(movie: Movie): Boolean {
        return _favorites.value.contains(movie)
    }

    // Filmleri sıralama
    fun sortMoviesBy(criteria: String) {
        viewModelScope.launch {
            val sortedList = when (criteria) {
                "Fiyat Artan" -> _movies.value.sortedBy { it.price }
                "Fiyat Azalan" -> _movies.value.sortedByDescending { it.price }
                "Puana Göre" -> _movies.value.sortedByDescending { it.rating }
                else -> _movies.value
            }
            _movies.value = sortedList
        }
    }

    // Kategoriye göre filmleri getir
    fun getMoviesByCategory(category: String): List<Movie> {
        return _movies.value.filter { it.category == category }
    }
}