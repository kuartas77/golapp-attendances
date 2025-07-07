package com.golapp.attendances.data.remote.dto

import android.os.Build
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestAuth(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("device_name") val deviceName: String = Build.MODEL.toString()
)
