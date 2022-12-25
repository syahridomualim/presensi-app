package com.syahrido.mualim.example.presensiapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syahrido.mualim.example.presensiapp.data.network.Resource
import com.syahrido.mualim.example.presensiapp.data.repository.EmployeeRepository
import com.syahrido.mualim.example.presensiapp.model.response.EmployeeResponse
import kotlinx.coroutines.launch


class ProfileViewModel(
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    private val _employee: MutableLiveData<Resource<EmployeeResponse>> = MutableLiveData()
    val employee: LiveData<Resource<EmployeeResponse>>
        get() = _employee

    fun getEmployee(idEmployee: Long) {
        viewModelScope.launch {
            _employee.value = Resource.Loading
            _employee.value = employeeRepository.getEmployee(idEmployee)
        }
    }

    fun logOut() {
        viewModelScope.launch {
            employeeRepository.logOut()
        }
    }

}