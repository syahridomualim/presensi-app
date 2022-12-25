package com.syahrido.mualim.example.presensiapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.syahrido.mualim.example.presensiapp.base.BaseRepository
import com.syahrido.mualim.example.presensiapp.data.EmployeePreferences
import com.syahrido.mualim.example.presensiapp.data.database.PresensiDao
import com.syahrido.mualim.example.presensiapp.data.network.EmployeeApi
import com.syahrido.mualim.example.presensiapp.data.network.Resource
import com.syahrido.mualim.example.presensiapp.mapper.PresensiMapper
import com.syahrido.mualim.example.presensiapp.model.UiPresensi
import com.syahrido.mualim.example.presensiapp.model.response.PresensiResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PresensiRepository(
    private val employeeApi: EmployeeApi,
    private val presensiDao: PresensiDao,
    private val presensiMapper: PresensiMapper,
    private val employeePreferences: EmployeePreferences,
) : BaseRepository() {

    fun getPresensi(): LiveData<Resource<List<UiPresensi>>> {
        val result = MutableLiveData<Resource<List<UiPresensi>>>()

        result.postValue(Resource.Loading)
        val idEmployee = runBlocking { employeePreferences.idEmployee.first() }
        employeeApi.checkAttendance(idEmployee!!)
            .enqueue(object : Callback<List<PresensiResponse>> {
                override fun onResponse(
                    call: Call<List<PresensiResponse>>,
                    response: Response<List<PresensiResponse>>,
                ) {
                    response.body()?.let { presensi ->
                        runBlocking {
                            presensiDao.insertPresensi(presensi.map {
                                presensiMapper.serviceToEntity(it)
                            })
                        }
                        result.postValue(Resource.Success(presensi.map {
                            presensiMapper.serviceToUi(it)
                        }))
                    }
                }

                override fun onFailure(call: Call<List<PresensiResponse>>, t: Throwable) {
                }
            })

        return result
    }
}