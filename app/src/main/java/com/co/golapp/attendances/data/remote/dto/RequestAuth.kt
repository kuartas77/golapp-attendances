package com.co.golapp.attendances.data.remote.dto

import android.os.Build
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestAuth(
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String,
    @field:Json(name = "device_name") val deviceName: String = Build.MODEL.toString()
)
