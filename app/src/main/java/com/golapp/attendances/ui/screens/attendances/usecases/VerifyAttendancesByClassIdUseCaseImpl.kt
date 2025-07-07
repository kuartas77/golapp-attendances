package com.golapp.attendances.ui.screens.attendances.usecases

import com.golapp.attendances.domain.models.Attendance
import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.domain.repository.AttendanceRepository
import com.golapp.attendances.domain.repository.GroupRepository
import com.golapp.attendances.domain.usecases.attendances.VerifyAttendancesByClassIdUseCase
import java.time.LocalDate
import javax.inject.Inject

class VerifyAttendancesByClassIdUseCaseImpl @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val groupRepository: GroupRepository
) : VerifyAttendancesByClassIdUseCase {
    override suspend fun invoke(classDay: ClassDay) {

        val groupWithPlayers = groupRepository.getGroupWhitPlayersById(classDay.groupId)
        val attendanceLocalList = attendanceRepository.getAttendancesWithPlayers(classDay)

        if (attendanceLocalList.isEmpty() || attendanceLocalList.size != groupWithPlayers.players.size) {
            val remoteAttendanceList = attendanceRepository.fetchAttendances(classDay)
            if (remoteAttendanceList.isEmpty()) {
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
                    attendanceRepository.insertAttendance(attendance)
                }
            } else {
                attendanceRepository.insertAttendances(remoteAttendanceList)
            }
        }
    }
}
