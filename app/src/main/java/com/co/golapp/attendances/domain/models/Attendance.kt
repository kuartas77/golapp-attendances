package com.co.golapp.attendances.domain.models

data class Attendance(
    override val id: Long?,
    override val attendanceId: Int?,
    override val schoolId: Int,
    override val trainingGroupId: Int,
    override val inscriptionId: Int,
    override val year: Int,
    override val month: Int,
    override val column: String,
    override var value: String?,
    override val playerId: Int
) : IAttendance
