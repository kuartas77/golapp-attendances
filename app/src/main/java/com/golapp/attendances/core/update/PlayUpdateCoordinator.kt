package com.golapp.attendances.core.update

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import timber.log.Timber

class PlayUpdateCoordinator private constructor(
    private val updateManager: AppUpdateManager,
    private val updateLauncher: ActivityResultLauncher<IntentSenderRequest>,
) {
    fun checkForUpdate() {
        updateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->
            if (ImmediateUpdatePolicy.shouldStart(updateInfo)) {
                startImmediateUpdate(updateInfo)
            }
        }.addOnFailureListener { error ->
            Timber.w(error, "No fue posible consultar actualizaciones en Google Play")
        }
    }

    fun resumeUpdateIfNeeded() {
        updateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->
            if (ImmediateUpdatePolicy.shouldResume(updateInfo)) {
                startImmediateUpdate(updateInfo)
            }
        }.addOnFailureListener { error ->
            Timber.w(error, "No fue posible reanudar la actualización de Google Play")
        }
    }

    private fun startImmediateUpdate(updateInfo: AppUpdateInfo) {
        runCatching {
            updateManager.startUpdateFlowForResult(
                updateInfo,
                updateLauncher,
                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
            )
        }.onFailure { error ->
            Timber.e(error, "No fue posible iniciar la actualización de Google Play")
        }
    }

    companion object {
        fun create(
            activity: Activity,
            updateLauncher: ActivityResultLauncher<IntentSenderRequest>,
        ): PlayUpdateCoordinator = PlayUpdateCoordinator(
            updateManager = AppUpdateManagerFactory.create(activity),
            updateLauncher = updateLauncher,
        )
    }
}

internal object ImmediateUpdatePolicy {
    fun shouldStart(updateInfo: AppUpdateInfo): Boolean = shouldStart(
        availability = updateInfo.updateAvailability(),
        immediateAllowed = updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE),
    )

    fun shouldStart(availability: Int, immediateAllowed: Boolean): Boolean =
        availability == UpdateAvailability.UPDATE_AVAILABLE && immediateAllowed

    fun shouldResume(updateInfo: AppUpdateInfo): Boolean =
        shouldResume(updateInfo.updateAvailability())

    fun shouldResume(availability: Int): Boolean =
        availability == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
}
