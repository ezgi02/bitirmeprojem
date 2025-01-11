package com.example.movieapi.retrofit

import com.example.movieapi.data.entity.ApiResponse
import com.example.movieapi.data.entity.MovieCartResponse
import com.example.movieapi.data.entity.MovieResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface MovieApi {

    //Tüm filmleri getir
    @GET("movies/getAllMovies.php")
    suspend fun getAllMovies(): MovieResponse

    //Sepete film ekleme
    @FormUrlEncoded
    @POST("movies/insertMovie.php")
    suspend fun insertMovieToCart(
        @Field("name") name: String,
        @Field("image") image: String,
        @Field("price") price: Int,
        @Field("category") category: String,
        @Field("rating") rating: Double,
        @Field("year") year: Int,
        @Field("director") director: String,
        @Field("description") description: String,
        @Field("orderAmount") orderAmount: Int,
        @Field("userName") userName: String
    ): ApiResponse//Ekleme işleminin sonucunu döner

    //Sepetteki filmleri getirme
    @FormUrlEncoded
    @POST("movies/getMovieCart.php")
    suspend fun getMovieCart(
        @Field("userName") userName: String
    ): MovieCartResponse

    // Sepetten film silme
    @FormUrlEncoded
    @POST("movies/deleteMovie.php")
    suspend fun deleteMovieFromCart(
        @Field("cartId") cartId: Int,
        @Field("userName") userName: String
    ): ApiResponse //Silme işleminin sonucunu döner

}