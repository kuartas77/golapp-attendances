package com.golapp.attendances.ui.screens.groups.usecases

import com.golapp.attendances.domain.models.Statistics
import com.golapp.attendances.domain.repository.GroupRepository
import com.golapp.attendances.domain.usecases.groups.GetStatisticsUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStatisticsUseCaseImpl @Inject constructor(
    private val groupRepository: GroupRepository
) : GetStatisticsUseCase {
    override suspend fun invoke(): Flow<List<Statistics>> = groupRepository.fetchStatistics()
}
