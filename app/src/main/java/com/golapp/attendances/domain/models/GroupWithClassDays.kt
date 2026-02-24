package com.golapp.attendances.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupWithClassDays(
    val group: Group,
    val classDays: List<ClassDay>,
) : Parcelable
