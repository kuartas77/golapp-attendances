package com.golapp.attendances.domain.models

interface IAttendance {
    val id: Long?
    val attendanceId: Int?
    val schoolId: Int
    val trainingGroupId: Int
    val inscriptionId: Int
    val year: Int
    val month: Int
    val column: String
    var value: String?
    val playerId: Int
}
