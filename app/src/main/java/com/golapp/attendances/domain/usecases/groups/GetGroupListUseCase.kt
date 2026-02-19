package com.golapp.attendances.domain.usecases.groups

import com.golapp.attendances.domain.models.GroupWithClassDays

interface GetGroupListUseCase {
    suspend operator fun invoke(month: Int): List<GroupWithClassDays>
}
