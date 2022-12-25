package com.syahrido.mualim.example.presensiapp.data.network

import androidx.lifecycle.LiveData
import com.syahrido.mualim.example.presensiapp.model.response.EmployeeResponse
import com.syahrido.mualim.example.presensiapp.model.response.PresensiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EmployeeApi {

    @GET("employee/{idEmployee}")
    suspend fun getEmployee(@Path("idEmployee") idEmployee: Long): EmployeeResponse

    @POST("employee/presensi/checkIn")
    suspend fun checkIn(
        @Query("idEmployee") idEmployee: Long,
        @Query("note") note: String,
        @Query("location") location: String
    )

    @POST("employee/presensi/checkOut")
    suspend fun checkOut(
        @Query("idEmployee") idEmployee: Long,
        @Query("location") location: String
    )

    @GET("employee/presensi/{idEmployee}")
    fun checkAttendance(@Path("idEmployee") idEmployee: Long): Call<List<PresensiResponse>>
}
