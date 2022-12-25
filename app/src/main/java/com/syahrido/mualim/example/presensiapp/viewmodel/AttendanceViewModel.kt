package com.syahrido.mualim.example.presensiapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syahrido.mualim.example.presensiapp.data.repository.AttendanceRepository
import com.syahrido.mualim.example.presensiapp.data.network.Resource
import kotlinx.coroutines.launch

class AttendanceViewModel(private val attendanceRepository: AttendanceRepository) : ViewModel() {

    fun checkIn(idEmployee: Long, note: String, location: String) {
        viewModelScope.launch {
            Resource.Loading
            attendanceRepository.checkIn(idEmployee, note, location)
        }
    }

    fun checkOut(idEmployee: Long, location: String) {
        viewModelScope.launch {
            Resource.Loading
            attendanceRepository.checkOut(idEmployee, location)
        }
    }
}
