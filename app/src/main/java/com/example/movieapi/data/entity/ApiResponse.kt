package com.example.movieapi.data.entity


//API'den dönen genel yanıtları temsil eder
data class ApiResponse(
    val success: Int,
    val message: String
)