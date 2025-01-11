package com.example.movieapi.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapi.data.entity.CartItem
import com.example.movieapi.data.entity.Movie
import com.example.movieapi.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel:ViewModel() {

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart

  /*  private val _userName = MutableStateFlow<String?>(null) // Kullanıcı adı

    fun setUserName(name: String) {
        _userName.value = name
    }*/
  private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    fun updateUserName(name: String) {
        _userName.value = name
    }
    //Sepetteki filmleri getir
    fun fetchCart(userName: String) {
        viewModelScope.launch {
            try {

                val response = RetrofitInstance.api.getMovieCart(userName)
                Log.d("CartViewModel", "API Response: $response")
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
    //Sepetten film silme
    fun removeFromCart(cartId: Int, userName: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.deleteMovieFromCart(cartId, userName)
                if (response.success == 1) {
                    fetchCart(userName) // Sepeti güncelle
                } else {
                    Log.e("CartViewModel", "Failed to remove movie: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error removing from cart", e)
            }
        }
    }
}