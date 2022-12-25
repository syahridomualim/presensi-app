package com.syahrido.mualim.example.presensiapp.data.repository

import com.syahrido.mualim.example.presensiapp.base.BaseRepository
import com.syahrido.mualim.example.presensiapp.data.network.EmployeeApi

class AttendanceRepository(
    private val employeeApi: EmployeeApi,
) : BaseRepository(){

    suspend fun checkIn(idEmployee: Long, note: String, location: String) = safeApiCall {
        employeeApi.checkIn(idEmployee, note, location)
    }

    suspend fun checkOut(idEmployee: Long, location: String) = safeApiCall {
        employeeApi.checkOut(idEmployee, location)
    }
}