package com.syahrido.mualim.example.presensiapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel() : ViewModel() {

    private var _location: MutableLiveData<String> = MutableLiveData()
    val location: LiveData<String> = _location

    fun getLocationText(value: String) {
        viewModelScope.launch {
            _location.postValue(value)
        }
    }
}