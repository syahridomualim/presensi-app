package com.syahrido.mualim.example.presensiapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syahrido.mualim.example.presensiapp.data.network.Resource
import com.syahrido.mualim.example.presensiapp.data.repository.PresensiRepository
import com.syahrido.mualim.example.presensiapp.model.UiPresensi
import kotlinx.coroutines.launch

class RecordViewModel(
    private val presensiRepository: PresensiRepository,
) : ViewModel() {

    fun getPresensi(): LiveData<Resource<List<UiPresensi>>> {
        return presensiRepository.getPresensi()
    }



}