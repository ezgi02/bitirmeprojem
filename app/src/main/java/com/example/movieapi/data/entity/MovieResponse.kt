package com.example.movieapi.data.entity

import com.example.movieapi.data.entity.Movie

//Tüm filmleri temsil eden veri sınıfı.
data class MovieResponse(
    val movies: List<Movie>
)