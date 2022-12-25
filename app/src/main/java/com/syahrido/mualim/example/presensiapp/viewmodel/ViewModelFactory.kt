package com.syahrido.mualim.example.presensiapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.syahrido.mualim.example.presensiapp.data.repository.AttendanceRepository
import com.syahrido.mualim.example.presensiapp.data.repository.AuthRepository
import com.syahrido.mualim.example.presensiapp.base.BaseRepository
import com.syahrido.mualim.example.presensiapp.data.repository.EmployeeRepository
import com.syahrido.mualim.example.presensiapp.data.repository.PresensiRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: BaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(repository as AuthRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository as EmployeeRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel() as T
            }
            modelClass.isAssignableFrom(AttendanceViewModel::class.java) -> {
                AttendanceViewModel(repository as AttendanceRepository) as T
            }
            modelClass.isAssignableFrom(RecordViewModel::class.java) -> {
                RecordViewModel(repository as PresensiRepository) as T
            }
            else -> {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}