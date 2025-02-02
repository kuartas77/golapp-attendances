package com.golapp.attendances.domain.usecases.groups

import com.golapp.attendances.domain.models.GroupWithClassDays
import kotlinx.coroutines.flow.Flow

interface GetGroupListUseCase {
    suspend operator fun invoke(month: Int): Flow<List<GroupWithClassDays>>
}
