package com.example.movieapi.data.entity

import com.example.movieapi.data.entity.CartItem

//Sepetteki filmleri temsil eden veri sınıfı.
data class MovieCartResponse(
    val movie_cart: List<CartItem>
)