package com.golapp.attendances.domain.usecases.attendances

import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.golapp.attendances.data.sync.AttendanceSyncWorker
import com.golapp.attendances.domain.models.AttendanceSync
import com.golapp.attendances.domain.repositories.AttendanceRepository

class SyncAttendanceUseCase(
    private val attendanceRepository: AttendanceRepository,
    private val workManager: WorkManager
) {
     suspend operator fun invoke() {
        val attendanceSyncList = attendanceRepository.getAllAttendances()

        if (attendanceSyncList.isEmpty()) return

        attendanceRepository.insertAttendancesSync(attendanceSyncList.map { AttendanceSync(it.id!!) })

        workManager.beginUniqueWork(
            AttendanceSyncWorker.TAG,
            ExistingWorkPolicy.REPLACE,
            AttendanceSyncWorker.oneTimeWorkRequest()
        ).enqueue()
    }
}