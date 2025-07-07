package com.golapp.attendances.ui.screens.attendances.usecases

import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.golapp.attendances.data.sync.AttendanceSyncWorker
import com.golapp.attendances.domain.models.AttendanceSync
import com.golapp.attendances.domain.repository.AttendanceRepository
import com.golapp.attendances.domain.usecases.attendances.SyncAttendanceUseCase

class SyncAttendanceUseCaseImpl(
    private val attendanceRepository: AttendanceRepository,
    private val workManager: WorkManager
) : SyncAttendanceUseCase {
    override suspend fun invoke() {
        val attendanceSyncList = attendanceRepository.getAllAttendances()
        attendanceSyncList.forEach { attendanceSync ->
            if (attendanceSync.id != null) {
                attendanceRepository.insertAttendanceSync(AttendanceSync(attendanceSync.id))
            }
        }

        workManager.beginUniqueWork(
            AttendanceSyncWorker.TAG,
            ExistingWorkPolicy.REPLACE,
            AttendanceSyncWorker.oneTimeWorkRequest()
        ).enqueue()
    }
}
