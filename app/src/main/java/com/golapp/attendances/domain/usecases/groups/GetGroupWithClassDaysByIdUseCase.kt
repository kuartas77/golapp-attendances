package com.golapp.attendances.domain.usecases.groups

import com.golapp.attendances.domain.models.GroupWithClassDays

interface GetGroupWithClassDaysByIdUseCase {
    suspend operator fun invoke(groupId: Int): GroupWithClassDays
}
