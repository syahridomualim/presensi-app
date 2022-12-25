package com.syahrido.mualim.example.presensiapp.data.network

import com.syahrido.mualim.example.presensiapp.model.request.LoginRequest
import com.syahrido.mualim.example.presensiapp.model.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST(value = "employee/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}