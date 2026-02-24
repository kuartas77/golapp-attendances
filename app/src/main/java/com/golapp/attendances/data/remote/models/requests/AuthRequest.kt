package com.golapp.attendances.data.remote.models.requests

import android.os.Build
import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("device_name") val deviceName: String = Build.MODEL.toString()
)
