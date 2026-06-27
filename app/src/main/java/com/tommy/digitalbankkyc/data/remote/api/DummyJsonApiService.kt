package com.tommy.digitalbankkyc.data.remote.api

import com.tommy.digitalbankkyc.data.remote.dto.UserResponseDto
import retrofit2.http.GET

interface DummyJsonApiService {
    @GET("users")
    suspend fun getUsers(): UserResponseDto
}
