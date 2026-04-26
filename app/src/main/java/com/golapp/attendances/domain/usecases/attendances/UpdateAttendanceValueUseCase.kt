package com.golapp.attendances.domain.usecases.attendances

import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.golapp.attendances.data.sync.AttendanceSyncWorker
import com.golapp.attendances.di.IoDispatcher
import com.golapp.attendances.domain.repositories.AttendanceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateAttendanceValueUseCase @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val workManager: WorkManager,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(localAttendanceId: Long, value: String?) =
        withContext(ioDispatcher) {
            attendanceRepository.updateAttendanceValue(localAttendanceId, value)
            if (attendanceRepository.getAllAttendanceSync().isNotEmpty()) {
                workManager.beginUniqueWork(
                    AttendanceSyncWorker.TAG,
                    ExistingWorkPolicy.KEEP,
                    AttendanceSyncWorker.oneTimeWorkRequest()
                ).enqueue()
            }
        }
}
