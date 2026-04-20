package com.golapp.attendances.data.repositories

import androidx.room.withTransaction
import com.golapp.attendances.data.datasources.AttendanceRemoteDataSource
import com.golapp.attendances.data.local.database.AttendancesDB
import com.golapp.attendances.data.local.database.daos.AttendanceDao
import com.golapp.attendances.data.mappers.toDomain
import com.golapp.attendances.data.mappers.toEntity
import com.golapp.attendances.data.remote.models.dtos.StatisticsDto
import com.golapp.attendances.di.IoDispatcher
import com.golapp.attendances.domain.models.Attendance
import com.golapp.attendances.domain.models.AttendanceSync
import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.domain.repositories.AttendanceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val db: AttendancesDB,
    private val attendanceDao: AttendanceDao,
    private val remote: AttendanceRemoteDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AttendanceRepository {

    override suspend fun getAttendancesWithPlayers(
        classDay: ClassDay,
        year: Int
    ): List<AttendanceWithPlayer> = withContext(ioDispatcher) {
        attendanceDao.getAttendancesWithPlayers(
            schoolId = classDay.schoolId,
            groupId = classDay.groupId,
            year = year,
            month = classDay.month,
            column = classDay.column
        ).map { it.toDomain() }
    }

    override fun observeAttendancesWithPlayers(
        classDay: ClassDay,
        year: Int
    ): Flow<List<AttendanceWithPlayer>> {
        return attendanceDao.observeAttendancesWithPlayers(
            schoolId = classDay.schoolId,
            groupId = classDay.groupId,
            year = year,
            month = classDay.month,
            column = classDay.column
        ).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun fetchAttendances(
        classDay: ClassDay,
        year: Int
    ): List<Attendance> = withContext(ioDispatcher) {
        try {
            // el remoto puede traer year o no; forzamos el year del contexto si hace falta
            remote.fetchAttendances(classDay).map { a ->
                if (a.year == year) a else a.copy(year = year)
            }
        } catch (e: Exception) {
            Timber.e(e, "fetchAttendances failed: ${e.message}")
            emptyList()
        }
    }

    override suspend fun upsertAttendances(items: List<Attendance>) = withContext(ioDispatcher) {
        if (items.isEmpty()) return@withContext

        // Para no romper el id autogenerado:
        // 1) INSERT IGNORE (inserta solo los nuevos)
        // 2) Para conflictos, UPDATE por llave natural (schoolId, groupId, inscriptionId, playerId, year, month, column)
        val entities = items.map { it.toEntity() }

        db.withTransaction {
            val insertResult = attendanceDao.insertAllIgnore(entities)

            // los que devolvieron -1L fueron conflictos => actualizamos por llave natural
            val toUpdate = entities.filterIndexed { idx, _ -> insertResult[idx] == -1L }
            for (e in toUpdate) {
                attendanceDao.updateByNaturalKey(
                    attendanceId = e.attendanceId,
                    value = e.value,
                    schoolId = e.schoolId,
                    groupId = e.trainingGroupId,
                    inscriptionId = e.inscriptionId,
                    playerId = e.playerId,
                    year = e.year,
                    month = e.month,
                    column = e.column
                )
            }
        }
    }

    override suspend fun insertAttendances(items: List<Attendance>) = withContext(ioDispatcher) {
        if (items.isEmpty()) return@withContext
        val entities = items.map { it.toEntity() }
        attendanceDao.insertAllIgnore(entities)
    }

    override suspend fun updateAttendanceValue(localAttendanceId: Long, value: String?) =
        withContext(ioDispatcher) {
            attendanceDao.updateValueById(localAttendanceId, value)
            try {
                val attendance = attendanceDao.getAttendanceById(localAttendanceId)?.toDomain()
                if (attendance != null) {
                    remote.syncAttendance(attendance)
                }
            } catch (e: Exception) {
                Timber.e(e, "syncAssignedGroups failed: ${e.message}")
                return@withContext
            }
        }

    override suspend fun getAllAttendances(): List<Attendance> = withContext(ioDispatcher) {
        attendanceDao.getAllAttendances().map { it.toDomain() }
    }

    override suspend fun getAllAttendanceSync(): List<AttendanceSync> = withContext(ioDispatcher) {
        attendanceDao.getAllAttendanceSync().map { it.toDomain() }
    }

    override suspend fun insertAttendancesSync(items: List<AttendanceSync>) {
        attendanceDao.insertAttendancesSync(items.map { it.toEntity() })
    }

    override suspend fun deleteAttendanceSync(item: AttendanceSync) {
        attendanceDao.deleteAttendanceSync(item.toEntity())
    }

    override suspend fun getAttendanceById(id: Long): Attendance? = withContext(ioDispatcher) {
        attendanceDao.getAttendanceById(id)?.toDomain()
    }

    override suspend fun syncAttendance(attendance: Attendance) {
        remote.syncAttendance(attendance)
    }

    override suspend fun getAttendanceStatistics(): List<StatisticsDto> {
        TODO("Not yet implemented")
    }

}