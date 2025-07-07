package com.golapp.attendances.domain.usecases.groups

import com.golapp.attendances.domain.models.Statistics
import kotlinx.coroutines.flow.Flow

interface GetStatisticsUseCase {
    suspend operator fun invoke(): Flow<List<Statistics>>

}
