package com.golapp.attendances.domain.usecases.attendances

import com.golapp.attendances.domain.models.Attendance
import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.domain.repositories.AttendanceRepository
import com.golapp.attendances.domain.repositories.GroupRepository
import com.golapp.attendances.domain.time.YearProvider
import javax.inject.Inject

class EnsureAttendancesForClassDayUseCase @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val groupRepository: GroupRepository,
    private val yearProvider: YearProvider
) {
    suspend operator fun invoke(classDay: ClassDay) {
        val year = yearProvider.currentYear()

        val groupWithPlayers = groupRepository.getGroupWhitPlayersById(classDay.groupId)
        val expectedPlayers = groupWithPlayers.players

        // Local (con players) para ese classDay + year
        val localAttendances = attendanceRepository.getAttendancesWithPlayers(
            classDay = classDay,
            year = year
        )

        // Si ya están todas, salimos
        val expectedPlayerIds = expectedPlayers.map { it.playerId }.toSet()
        val localPlayerIds = localAttendances.map { it.playerId }.toSet()
        val missing = expectedPlayerIds - localPlayerIds
        if (missing.isEmpty()) return

        // Intentar remoto solo si faltan
        val remoteAttendances = attendanceRepository.fetchAttendances(
            classDay = classDay,
            year = year
        )

        if (remoteAttendances.isNotEmpty()) {
            attendanceRepository.upsertAttendances(remoteAttendances)
            return
        }

        // Si remoto no trae nada: placeholders SOLO para faltantes
        val placeholders = expectedPlayers
            .asSequence()
            .filter { it.playerId in missing }
            .map { player ->
                Attendance(
                    id = null,
                    attendanceId = null,
                    schoolId = classDay.schoolId,
                    trainingGroupId = classDay.groupId,
                    inscriptionId = player.inscriptionId,
                    year = year,
                    month = classDay.month,
                    column = classDay.column,
                    value = null,
                    playerId = player.playerId
                )
            }
            .toList()

        if (placeholders.isNotEmpty()) {
            attendanceRepository.insertAttendances(placeholders)
        }
    }
}