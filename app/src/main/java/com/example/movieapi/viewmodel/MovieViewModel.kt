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

    //Kullanıcı adı bilgisi
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    // Tüm filmler
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    // Sepet içeriği
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart

    // Arama metni
    private val _searchQuery = MutableStateFlow("") // Arama metni
    val searchQuery: StateFlow<String> = _searchQuery

    // Yönetmen filtresi
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

    // Favori filmler
    private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
    val favorites: StateFlow<List<Movie>> = _favorites

    // Kullanıcı adı güncelleme
    fun updateUserName(name: String) {
        _userName.value = name
    }
    // Arama metnini güncelleme
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Yönetmen filtresini güncelleme
    fun updateFilterDirector(director: String?) {
        _filterDirector.value = director
    }
    // ViewModel ilk çalıştığında filmleri getir
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

    // Kullanıcıya ait sepet içeriğini getir
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

  /*  // Sepetten film sil
    fun removeFromCart(cartId: Int, userName: String) {
        viewModelScope.launch {
            try {
                repository.deleteMovieFromCart(cartId, userName)
                fetchCart(userName) // Sepeti güncelle
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error removing from cart", e)
            }
        }
    }*/
  fun removeAllFromCart(productName: String, userName: String) {
      viewModelScope.launch {
          try {
              // Sepetteki aynı ürünlerden hepsini silmek için filtreleme
              val itemsToRemove = _cart.value.filter { it.name == productName }
              itemsToRemove.forEach { cartItem ->
                  repository.deleteMovieFromCart(cartItem.cartId, userName)
              }

              // Sepeti güncelle
              fetchCart(userName)
          } catch (e: Exception) {
              Log.e("MovieViewModel", "Error removing all items from cart", e)
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