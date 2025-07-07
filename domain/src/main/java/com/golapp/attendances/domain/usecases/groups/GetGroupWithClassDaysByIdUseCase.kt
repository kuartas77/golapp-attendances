package com.golapp.attendances.domain.usecases.groups

import com.golapp.attendances.domain.models.GroupWithClassDays
import kotlinx.coroutines.flow.Flow

interface GetGroupWithClassDaysByIdUseCase {
    suspend operator fun invoke(groupId: Int): Flow<GroupWithClassDays>
}
