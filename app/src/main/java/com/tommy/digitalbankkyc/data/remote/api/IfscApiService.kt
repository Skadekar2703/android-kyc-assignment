package com.tommy.digitalbankkyc.data.remote.api

import com.tommy.digitalbankkyc.data.remote.dto.IfscResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface IfscApiService {
    @GET("{ifsc}")
    suspend fun getBankInfo(@Path("ifsc") ifsc: String): IfscResponseDto
}
