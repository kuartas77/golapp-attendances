package com.golapp.attendances.data.remote.models.responses

import com.google.gson.annotations.SerializedName

data class ApiData<T>(
    @SerializedName("data") val data: T?
)
