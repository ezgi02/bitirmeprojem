package com.example.movieapi.data.datasource



import com.example.movieapi.data.entity.ApiResponse
import com.example.movieapi.data.entity.MovieCartResponse
import com.example.movieapi.data.entity.MovieResponse
import com.example.movieapi.retrofit.MovieApi

class MovieDataSource(private val api: MovieApi) {

    //Tüm film listesini almak
    suspend fun getAllMovies(): MovieResponse {
        return api.getAllMovies()
    }

    //Bir filmi kullanıcı sepetine eklemek.

    suspend fun insertMovieToCart(
        name: String,
        image: String,
        price: Int,
        category: String,
        rating: Double,
        year: Int,
        director: String,
        description: String,
        orderAmount: Int,
        userName: String
    ): ApiResponse {
        return api.insertMovieToCart(
            name, image, price, category, rating, year, director, description, orderAmount, userName
        )
    }
    //Kullanıcının sepetindeki filmleri almak.
    suspend fun getMovieCart(userName: String): MovieCartResponse {
        return api.getMovieCart(userName)
    }
    //Kullanıcının sepetinden bir filmi silmek.
    suspend fun deleteMovieFromCart(cartId: Int, userName: String): ApiResponse {
        return api.deleteMovieFromCart(cartId, userName)
    }
}
