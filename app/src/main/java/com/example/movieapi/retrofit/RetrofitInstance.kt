package com.example.movieapi.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    // API'nin temel URL'si
    private const val BASE_URL = "http://kasimadalan.pe.hu/"

    // Retrofit API arayüzü örneği
    val api: MovieApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApi::class.java)
    }
}