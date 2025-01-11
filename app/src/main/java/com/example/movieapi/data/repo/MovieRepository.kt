package com.example.movieapi.data.repo



import com.example.movieapi.data.entity.ApiResponse
import com.example.movieapi.data.entity.Movie
import com.example.movieapi.data.entity.MovieCartResponse
import com.example.movieapi.data.datasource.MovieDataSource

class MovieRepository(private val dataSource: MovieDataSource) {

    suspend fun getAllMovies(): List<Movie> {
        val response = dataSource.getAllMovies()
        return response.movies ?: emptyList()
    }

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
        return dataSource.insertMovieToCart(
            name, image, price, category, rating, year, director, description, orderAmount, userName
        )
    }

    suspend fun getMovieCart(userName: String): MovieCartResponse {
        return dataSource.getMovieCart(userName)
    }

    suspend fun deleteMovieFromCart(cartId: Int, userName: String): ApiResponse {
        return dataSource.deleteMovieFromCart(cartId, userName)
    }
}
