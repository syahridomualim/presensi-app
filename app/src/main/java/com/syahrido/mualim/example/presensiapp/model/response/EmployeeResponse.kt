package com.syahrido.mualim.example.presensiapp.model.response

data class EmployeeResponse(
    val idEmployee: Long,
    val name: String,
    val email: String,
    val profileImageUrl: String?,
    val area: AreaResponse?,
    val department: DepartmentResponse?
)