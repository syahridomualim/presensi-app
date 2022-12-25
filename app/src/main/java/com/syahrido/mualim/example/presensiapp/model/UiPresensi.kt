package com.syahrido.mualim.example.presensiapp.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class UiPresensi(
    val date: Long,

    val timeIn: Long,

    val timeOut: Long,

    val note: String,

    val locationIn: String?,

    val locationOut: String?
)