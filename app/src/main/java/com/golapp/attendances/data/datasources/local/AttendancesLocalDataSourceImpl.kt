package com.golapp.attendances.data.datasources.local

import com.golapp.attendances.data.local.dao.AttendanceDao
import com.golapp.attendances.data.local.datasources.AttendancesLocalDataSource
import com.golapp.attendances.data.local.models.AttendanceEntity
import com.golapp.attendances.data.local.models.AttendanceSyncEntity
import com.golapp.attendances.data.mappers.asDomain
import com.golapp.attendances.domain.models.AttendanceSync
import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.domain.models.ClassDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AttendancesLocalDataSourceImpl @Inject constructor(
    private val attendanceDao: AttendanceDao
) : AttendancesLocalDataSource {
    override suspend fun insertAttendance(attendance: AttendanceEntity) =
        attendanceDao.insertAttendance(attendance)

    override suspend fun insertAttendances(attendances: List<AttendanceEntity>) =
        attendanceDao.insertAttendances(attendances)

    override suspend fun insertAttendanceSync(attendanceSync: AttendanceSyncEntity) =
        attendanceDao.insertAttendanceSync(attendanceSync)

    override suspend fun getAttendancesWithPlayers(
        groupId: Int,
        month: Int,
        column: String,
        schoolId: Int
    ): List<AttendanceWithPlayer> =
        attendanceDao.getAttendancesWithPlayers(groupId, month, column, schoolId).asDomain()

    override suspend fun getAttendancesSync(): List<AttendanceSync> =
        attendanceDao.getAttendancesSync().asDomain()

    override suspend fun deleteAttendance(attendance: AttendanceEntity) =
        attendanceDao.deleteAttendance(attendance)

    override suspend fun deleteAttendanceSync(attendanceSyncEntity: AttendanceSyncEntity) =
        attendanceDao.deleteAttendanceSync(attendanceSyncEntity)

    override suspend fun deleteAttendances() = attendanceDao.deleteAttendances()

    override fun getAttendances(classDay: ClassDay): Flow<List<AttendanceWithPlayer>> =
        attendanceDao.getAttendances(
            classDay.groupId,
            classDay.month,
            classDay.column,
            classDay.schoolId
        ).map { it.asDomain() }

    override suspend fun getAttendanceById(id: Long): AttendanceEntity =
        attendanceDao.getAttendanceById(id)

    override suspend fun getAllAttendances(): List<AttendanceEntity> =
        attendanceDao.getAllAttendances()


}
