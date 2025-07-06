package com.golapp.attendances.data.repository

import androidx.annotation.WorkerThread
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.golapp.attendances.data.local.datasources.AttendancesLocalDataSource
import com.golapp.attendances.data.local.datasources.ClassDayLocalDataSource
import com.golapp.attendances.data.local.datasources.GroupsLocalDataSource
import com.golapp.attendances.data.mappers.asDomain
import com.golapp.attendances.data.mappers.asEntity
import com.golapp.attendances.data.remote.datasources.AttendancesRemoteDataSource
import com.golapp.attendances.data.remote.dto.RequestAttendance
import com.golapp.attendances.domain.models.Attendance
import com.golapp.attendances.domain.models.AttendanceSync
import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.domain.repository.AttendanceRepository
import com.golapp.attendances.domain.sync.AttendanceSyncWorker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@WorkerThread
class AttendanceRepositoryImpl @Inject constructor(
    private val attendanceLocalDataSource: AttendancesLocalDataSource,
    private val attendanceRemoteDataSource: AttendancesRemoteDataSource,
    private val workManager: WorkManager
) : AttendanceRepository {
    override suspend fun insertAttendance(attendance: Attendance) {
        val attendanceEntity = attendance.asEntity()
        attendanceLocalDataSource.insertAttendance(attendanceEntity)
    }

    override suspend fun insertAttendanceSync(attendanceSync: AttendanceSync) {
        val attendanceSyncEntity = attendanceSync.asEntity()
        attendanceLocalDataSource.insertAttendanceSync(attendanceSyncEntity)
    }

    override suspend fun insertAttendances(attendances: List<Attendance>) {
        val attendanceEntities = attendances.asEntity()
        attendanceLocalDataSource.insertAttendances(attendanceEntities)
    }

    override suspend fun getAttendanceById(id: Long): Attendance {
        val attendanceEntity = attendanceLocalDataSource.getAttendanceById(id)
        return attendanceEntity.asDomain()
    }

    override suspend fun getAttendancesSync(): List<AttendanceSync> {
        return attendanceLocalDataSource.getAttendancesSync()
    }

    override suspend fun getAttendancesWithPlayers(classDay: ClassDay): List<AttendanceWithPlayer> {
        return attendanceLocalDataSource.getAttendancesWithPlayers(
            classDay.groupId, classDay.month,
            classDay.column,
            classDay.schoolId
        )
    }

    override suspend fun getAttendances(classDay: ClassDay): Flow<List<AttendanceWithPlayer>> =
        attendanceLocalDataSource.getAttendances(classDay)


    override suspend fun deleteAttendance(attendance: Attendance) {
        attendanceLocalDataSource.deleteAttendance(attendance.asEntity())
    }

    override suspend fun deleteAllAttendances() {
        attendanceLocalDataSource.deleteAttendances()
    }

    override suspend fun deleteAttendanceSync(attendanceSync: AttendanceSync) {
        attendanceLocalDataSource.deleteAttendanceSync(attendanceSync.asEntity())
    }

    override suspend fun sendAttendance(requestAttendance: RequestAttendance) {
        attendanceRemoteDataSource.sendAttendance(requestAttendance)
    }

    override suspend fun syncAttendances() {
        val attendanceSyncList = attendanceLocalDataSource.getAllAttendances()
        attendanceSyncList.forEach { attendanceSync ->
            if (attendanceSync.id != null) {
                insertAttendanceSync(AttendanceSync(attendanceSync.id))
            }
        }

        workManager.beginUniqueWork(
            AttendanceSyncWorker.TAG,
            ExistingWorkPolicy.REPLACE,
            AttendanceSyncWorker.oneTimeWorkRequest()
        ).enqueue()
    }

    override suspend fun fetchAttendances(classDay: ClassDay): List<Attendance> {
        val attendanceEntities = attendanceRemoteDataSource.fetchAttendances(classDay)
        return attendanceEntities.asDomain()
    }

}
