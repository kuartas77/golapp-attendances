package com.golapp.attendances.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.golapp.attendances.data.util.resultOf
import com.golapp.attendances.domain.models.AttendanceSync
import com.golapp.attendances.domain.repositories.AttendanceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import timber.log.Timber
import java.time.Duration

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

        val items = attendanceRepository.getAllAttendanceSync()

        return try {
            supervisorScope {
                val jobs = items.map { item -> async { synchronize(item) } }
                jobs.awaitAll()
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    private suspend fun synchronize(item: AttendanceSync) {
        val attendance = attendanceRepository.getAttendanceById(item.id)
        if (attendance == null) {
            attendanceRepository.deleteAttendanceSync(item)
            return
        }

        resultOf {
            check(attendanceRepository.syncAttendance(attendance)) {
                "Server rejected attendance sync for ${item.id}"
            }
        }.onSuccess {
            attendanceRepository.deleteAttendanceSync(item)
        }.onFailure {
            Timber.e(it, "syncAttendance ${item.id} failed (remote)")
            throw it
        }
    }

    companion object {
        const val TAG = "sync_attendance_id"

        fun oneTimeWorkRequest(): OneTimeWorkRequest {
            val constrains = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            return OneTimeWorkRequestBuilder<AttendanceSyncWorker>()
                .setConstraints(constrains)
                .addTag(TAG)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, Duration.ofMinutes(5))
                .build()
        }
    }

}
