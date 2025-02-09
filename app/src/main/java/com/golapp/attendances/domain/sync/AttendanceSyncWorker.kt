package com.golapp.attendances.domain.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.golapp.attendances.common.resultOf
import com.golapp.attendances.data.mappers.asRequest
import com.golapp.attendances.domain.models.AttendanceSync
import com.golapp.attendances.domain.repository.AttendanceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

@HiltWorker
class AttendanceSyncWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val workerParameters: WorkerParameters,
    private val attendanceRepository: AttendanceRepository
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        if (runAttemptCount >= 3) {
            return Result.failure()
        }

        val items = attendanceRepository.getAttendancesSync()

        return try {
            supervisorScope {
                val jobs = items.map { item -> async { sync(item) } }
                jobs.awaitAll()
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    private suspend fun sync(item: AttendanceSync) {
        val attendance = attendanceRepository.getAttendanceById(item.id).asRequest()
        resultOf {
            attendanceRepository.sendAttendance(attendance)
        }.onSuccess {
            attendanceRepository.deleteAttendanceSync(item)
        }.onFailure {
            throw it
        }
    }

}
