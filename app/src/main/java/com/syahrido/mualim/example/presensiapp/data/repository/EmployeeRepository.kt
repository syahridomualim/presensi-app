package com.syahrido.mualim.example.presensiapp.data.repository

import com.syahrido.mualim.example.presensiapp.base.BaseRepository
import com.syahrido.mualim.example.presensiapp.data.EmployeePreferences
import com.syahrido.mualim.example.presensiapp.data.network.EmployeeApi

class EmployeeRepository(
    private val employeeApi: EmployeeApi,
    private val employeePreferences: EmployeePreferences
) : BaseRepository() {

    suspend fun getEmployee(idEmployee: Long) = safeApiCall {
        employeeApi.getEmployee(idEmployee)
    }

    suspend fun logOut() = safeApiCall {
        employeePreferences.clear()
    }

}