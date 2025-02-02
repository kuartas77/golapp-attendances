package com.co.golapp.attendances.data.repository

import androidx.annotation.WorkerThread
import com.co.golapp.attendances.common.di.IoDispatcher
import com.co.golapp.attendances.data.local.datasources.AttendancesLocalDataSource
import com.co.golapp.attendances.data.local.datasources.ClassDayLocalDataSource
import com.co.golapp.attendances.data.local.datasources.GroupsLocalDataSource
import com.co.golapp.attendances.data.mappers.asDomain
import com.co.golapp.attendances.data.mappers.asEntity
import com.co.golapp.attendances.data.remote.datasources.AttendancesRemoteDataSource
import com.co.golapp.attendances.domain.models.Attendance
import com.co.golapp.attendances.domain.models.AttendanceSync
import com.co.golapp.attendances.domain.models.AttendanceWithPlayer
import com.co.golapp.attendances.domain.models.ClassDay
import com.co.golapp.attendances.domain.repository.AttendanceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate
import javax.inject.Inject

@WorkerThread
class AttendanceRepositoryImpl @Inject constructor(
    private val attendanceLocalDataSource: AttendancesLocalDataSource,
    private val attendanceRemoteDataSource: AttendancesRemoteDataSource,
    private val classDayLocalDataSource: ClassDayLocalDataSource,
    private val groupLocalDataSource: GroupsLocalDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AttendanceRepository {

    override suspend fun verifyAttendancesByClassDayId(classDay: ClassDay) {
        val groupWithPlayers = groupLocalDataSource.getGroupWhitPlayersById(classDay.groupId)
        val attendanceLocalList = attendanceLocalDataSource.getAttendancesWithPlayers(
            classDay.groupId,
            classDay.month,
            classDay.column,
            classDay.schoolId
        )

        if (attendanceLocalList.isEmpty()) {
            attendanceRemoteDataSource.fetchAttendances(
                classDay.groupId,
                classDay.month,
                classDay.column,
                classDay.schoolId
            ).flowOn(ioDispatcher).collect { attendanceList ->

                if (attendanceList.isEmpty()) {
                    val currentYear = LocalDate.now().year
                    groupWithPlayers.players.forEach { player ->
                        val attendance = Attendance(
                            id = null,
                            attendanceId = null,
                            schoolId = classDay.schoolId,
                            trainingGroupId = classDay.groupId,
                            inscriptionId = player.inscriptionId,
                            year = currentYear,
                            month = classDay.month,
                            column = classDay.column,
                            value = null,
                            playerId = player.playerId
                        )
                        insertAttendance(attendance)
                    }
                }else {
                    insertAttendances(attendanceList.asDomain())
                }
            }
        }

    }

    override suspend fun getClassDayById(id: String): ClassDay =
        classDayLocalDataSource.getClassDayById(id)

    override fun getAttendances(classDay: ClassDay): Flow<List<AttendanceWithPlayer>> =
        attendanceLocalDataSource.getAttendances(
            classDay.groupId,
            classDay.month,
            classDay.column,
            classDay.schoolId
        )

    override suspend fun insertAttendance(attendance: Attendance) =
        attendanceLocalDataSource.insertAttendance(attendance.asEntity())

    override suspend fun insertAttendanceSync(attendanceSync: AttendanceSync) =
        attendanceLocalDataSource.insertAttendanceSync(attendanceSync.asEntity())

    override suspend fun insertAttendances(attendances: List<Attendance>) =
        attendanceLocalDataSource.insertAttendances(attendances.map { it.asEntity() })

    override suspend fun deleteAttendance(attendance: Attendance) =
        attendanceLocalDataSource.deleteAttendance(attendance.asEntity())

    override suspend fun deleteAttendances() = attendanceLocalDataSource.deleteAttendances()
}
