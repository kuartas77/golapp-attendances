package com.golapp.attendances.ui.screens.attendances.usecases

import com.golapp.attendances.common.remote.NetworkMonitor
import com.golapp.attendances.data.remote.dto.RequestAttendance
import com.golapp.attendances.domain.models.Attendance
import com.golapp.attendances.domain.models.AttendanceSync
import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.domain.repository.AttendanceRepository
import com.golapp.attendances.domain.usecases.attendances.TakeAttendanceUseCase
import javax.inject.Inject

class TakeAttendanceUseCaseImpl @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val networkMonitor: NetworkMonitor
) : TakeAttendanceUseCase {
    override suspend fun invoke(attendanceWithPlayer: AttendanceWithPlayer) {

        val attendance = Attendance(
            id = attendanceWithPlayer.id,
            attendanceId = attendanceWithPlayer.attendanceId,
            schoolId = attendanceWithPlayer.schoolId,
            trainingGroupId = attendanceWithPlayer.trainingGroupId,
            inscriptionId = attendanceWithPlayer.inscriptionId,
            year = attendanceWithPlayer.year,
            month = attendanceWithPlayer.month,
            column = attendanceWithPlayer.column,
            value = attendanceWithPlayer.value,
            playerId = attendanceWithPlayer.playerId
        )

        attendanceRepository.insertAttendance(attendance)

        if (networkMonitor.isConnected()){
            attendanceRepository.sendAttendance(
                RequestAttendance(
                    inscriptionId = attendance.inscriptionId,
                    groupId = attendance.trainingGroupId,
                    year = attendance.year,
                    month = attendance.month,
                    column = attendance.column,
                    value = attendance.value.toString(),
                    attendanceId = attendance.attendanceId,
                    attendanceDate = null,
                    observations = null,
                )
            )
        }else {
            attendance.id?.let {
                attendanceRepository.insertAttendanceSync(AttendanceSync(id = it))
            }
        }



    }

}
