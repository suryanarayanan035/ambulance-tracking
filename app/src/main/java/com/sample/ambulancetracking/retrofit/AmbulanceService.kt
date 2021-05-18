package com.sample.ambulancetracking.retrofit

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AmbulanceService {
//
//    @POST("/user")
//    suspend fun signUp(@Body payload: RegisterRequest): Response

    @GET("/user/{userId}")
    suspend fun check(@Path("userId") userId: String): CheckResponse
//
//    @POST("/user/login")
//    suspend fun login(@Body payload: LoginRequest): Response
}