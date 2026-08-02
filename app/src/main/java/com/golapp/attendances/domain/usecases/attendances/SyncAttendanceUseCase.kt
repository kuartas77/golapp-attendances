package com.golapp.attendances.domain.usecases.attendances

import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.golapp.attendances.core.workers.AttendanceSyncWorker
import com.golapp.attendances.domain.models.AttendanceSync
import com.golapp.attendances.domain.repositories.AttendanceRepository
import javax.inject.Inject

class SyncAttendanceUseCase @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val workManager: WorkManager
) {
    suspend operator fun invoke() {
        val attendanceSyncList = attendanceRepository.getAllAttendances()

        if (attendanceSyncList.isEmpty()) return

        attendanceRepository.insertAttendancesSync(
            attendanceSyncList.mapNotNull { attendance ->
                attendance.id?.let { AttendanceSync(it) }
            }
        )

        workManager.beginUniqueWork(
            AttendanceSyncWorker.TAG,
            ExistingWorkPolicy.KEEP,
            AttendanceSyncWorker.oneTimeWorkRequest()
        ).enqueue()
    }
}
