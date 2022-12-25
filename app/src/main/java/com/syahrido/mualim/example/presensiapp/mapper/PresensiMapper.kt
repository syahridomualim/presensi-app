package com.syahrido.mualim.example.presensiapp.mapper

import com.syahrido.mualim.example.presensiapp.data.database.PresensiEntity
import com.syahrido.mualim.example.presensiapp.model.UiPresensi
import com.syahrido.mualim.example.presensiapp.model.response.PresensiResponse

class PresensiMapper {

    fun serviceToEntity(presensiResponse: PresensiResponse) : PresensiEntity {
        return PresensiEntity(
            date = presensiResponse.date,
            timeIn =  presensiResponse.timeIn,
            timeOut = presensiResponse.timeOut,
            note = presensiResponse.note,
            locationIn = presensiResponse.locationIn,
            locationOut = presensiResponse.locationOut
        )
    }

    fun serviceToUi(presensiResponse: PresensiResponse) : UiPresensi {
        return UiPresensi(
            date = presensiResponse.date,
            timeIn =  presensiResponse.timeIn,
            timeOut = presensiResponse.timeOut,
            note = presensiResponse.note,
            locationIn = presensiResponse.locationIn,
            locationOut = presensiResponse.locationOut
        )
    }
}