package com.syahrido.mualim.example.presensiapp.data.repository

import com.syahrido.mualim.example.presensiapp.base.BaseRepository
import com.syahrido.mualim.example.presensiapp.data.EmployeePreferences
import com.syahrido.mualim.example.presensiapp.data.network.AuthApi
import com.syahrido.mualim.example.presensiapp.model.request.LoginRequest

class AuthRepository(
    private val api: AuthApi,
    private val employeePreferences: EmployeePreferences
) : BaseRepository() {

    suspend fun login(loginRequest: LoginRequest) = safeApiCall {
        api.login(loginRequest)
    }

    suspend fun saveToken(token: String) {
        employeePreferences.saveToken(token)
    }

    suspend fun saveIdEmployee(idEmployee: Long) {
        employeePreferences.saveIdEmployee(idEmployee)
    }
}