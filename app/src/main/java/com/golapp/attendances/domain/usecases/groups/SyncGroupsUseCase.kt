package com.golapp.attendances.domain.usecases.groups

import com.golapp.attendances.domain.models.GroupWithClassDays
import kotlinx.coroutines.flow.Flow

interface SyncGroupsUseCase {
    suspend operator fun invoke(): Flow<List<GroupWithClassDays>>
}
