package com.syahrido.mualim.example.presensiapp.model.response

import java.text.SimpleDateFormat
import java.util.*

data class PresensiResponse(
    val date: Long,
    val timeIn: Long,
    val timeOut: Long,
    val note: String,
    val locationIn: String,
    val locationOut: String
)