package com.golapp.attendances.data.remote.models.responses

import com.golapp.attendances.data.remote.models.dtos.GroupDto
import com.google.gson.annotations.SerializedName

data class GroupsResponse(
    @SerializedName("data")
    val group: List<GroupDto>
)
