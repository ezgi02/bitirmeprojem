package com.example.movieapi.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapi.data.CartItem
import com.example.movieapi.data.Movie
import com.example.movieapi.data.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
  /*  private val _userName = MutableStateFlow<String?>(null) // Kullanıcı adı

    fun setUserName(name: String) {
        _userName.value = name
    }*/
  private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    fun updateUserName(name: String) {
        _userName.value = name
    }
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart

    private val _searchQuery = MutableStateFlow("") // Arama metni
    val searchQuery: StateFlow<String> = _searchQuery

 /*   // Filtrelenmiş filmler
    val filteredMovies: StateFlow<List<Movie>> = combine(_movies, _searchQuery) { movies, query ->
        if (query.isEmpty()) {
            movies
        } else {
            movies.filter { it.name.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }*/
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
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFilterDirector(director: String?) {
        _filterDirector.value = director
    }

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
    //Sepetteki filmleri getirme
    fun fetchCart(userName: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getMovieCart(userName)
                _cart.value = response.movie_cart?: emptyList() // Boş liste durumu
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error fetching cart", e)
                _cart.value = emptyList() // Hata durumunda listeyi boş yap
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

    //Favoroielre ekle çıkar
    private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
    val favorites: StateFlow<List<Movie>> = _favorites

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
        Log.d("Favorites", "Current favorites: ${_favorites.value}") // Eklenen log

    }

    // Filmin favori olup olmadığını kontrol et
    fun isFavorite(movie: Movie): Boolean {
        return _favorites.value.contains(movie)
    }

    private val _filteredMovies = MutableStateFlow<List<Movie>>(emptyList())

//filmleri sıralama
fun sortMoviesBy(criteria: String) {
    viewModelScope.launch {
        val sortedList = when (criteria) {
            "Fiyat Artan" -> _movies.value.sortedBy { it.price } // Fiyata göre artan
            "Fiyat Azalan" -> _movies.value.sortedByDescending { it.price } // Fiyata göre azalan
            "Puana Göre" -> _movies.value.sortedByDescending { it.rating } // Puana göre azalan
            else -> _movies.value // Varsayılan liste
        }
        Log.d("SortMoviesBy", "Sıralama Kriteri: $criteria")
        Log.d("SortMoviesBy", "Sıralanmış Filmler: ${sortedList.joinToString { it.name }}")

        _movies.value = sortedList // Ana listeyi güncelle
    }
}

    //kategoriye ait filmler
    fun getMoviesByCategory(category: String): List<Movie> {
        return _movies.value.filter { it.category == category }
    }
    //Sepetten film silme
    fun removeFromCart(cartId: Int, userName: String) {
        if (userName.isEmpty()) {
            Log.e("MovieViewModel", "Cannot remove item. UserName is empty.")
            return // Eğer kullanıcı adı boşsa işlemi durdur
        }

        // Geçerli bir cartId kontrolü
        if (cartId <= 0) {
            Log.e("MovieViewModel", "Invalid Cart ID: $cartId")
            return // Eğer geçerli bir cartId yoksa işlemi durdur
        }

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.deleteMovieFromCart(cartId, userName)
                if (response.success == 1) {
                    fetchCart(userName) // Sepeti güncelle
                    // Sepet tamamen boşsa manuel olarak boş listeye set et
                    if (_cart.value.isEmpty()) {
                        _cart.value = emptyList()
                    }
                } else {
                    Log.e("MovieViewModel", "Failed to remove movie: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error removing from cart", e)
            }
        }
    }

}