package com.syahrido.mualim.example.presensiapp.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "presensi")
data class PresensiEntity(
    @PrimaryKey
    val date: Long,

    @ColumnInfo(name = "time_in")
    val timeIn: Long?,

    @ColumnInfo(name = "time_out")
    val timeOut: Long?,

    @ColumnInfo(name = "note")
    val note: String,

    @ColumnInfo(name = "location_in")
    val locationIn: String?,

    @ColumnInfo(name = "location_out")
    val locationOut: String?,
)