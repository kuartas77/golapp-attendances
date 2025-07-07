package com.golapp.attendances.domain.usecases.attendances

interface SendAttendanceTaken {
    suspend operator fun invoke(
        attendanceId: Int,
        groupId: Int,
        year: Int,
        month: Int,
        column: String,
        value: String,
        id: Int?,
        attendanceDate: String?,
        observations: String?
    )
}
