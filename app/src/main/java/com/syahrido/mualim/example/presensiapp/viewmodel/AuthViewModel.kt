package com.syahrido.mualim.example.presensiapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syahrido.mualim.example.presensiapp.data.network.Resource
import com.syahrido.mualim.example.presensiapp.data.repository.AuthRepository
import com.syahrido.mualim.example.presensiapp.model.request.LoginRequest
import com.syahrido.mualim.example.presensiapp.model.response.LoginResponse
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>>
        get() = _loginResponse

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            _loginResponse.value = Resource.Loading
            _loginResponse.value = authRepository.login(loginRequest)
        }
    }

    suspend fun saveToken(token: String) {
        authRepository.saveToken(token)
    }

    suspend fun saveIdEmployee(idEmployee: Long) {
        authRepository.saveIdEmployee(idEmployee)
    }
}