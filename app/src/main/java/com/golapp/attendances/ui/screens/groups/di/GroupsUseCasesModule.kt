package com.golapp.attendances.ui.screens.groups.di

import com.golapp.attendances.domain.repository.GroupRepository
import com.golapp.attendances.ui.screens.groups.usecases.GetGroupListUseCaseImpl
import com.golapp.attendances.ui.screens.groups.usecases.GetGroupWithClassDaysByIdUseCaseImpl
import com.golapp.attendances.ui.screens.groups.usecases.GetGroupWithPlayersByIdUseCaseImpl
import com.golapp.attendances.ui.screens.groups.usecases.GetStatisticsUseCaseImpl
import com.golapp.attendances.ui.screens.groups.usecases.GroupUseCases
import com.golapp.attendances.ui.screens.groups.usecases.SyncGroupsUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GroupsUseCasesModule {

    @Provides
    @Singleton
    fun provideGroupUseCases(groupRepository: GroupRepository): GroupUseCases {
        return GroupUseCases(
            syncGroups = SyncGroupsUseCaseImpl(groupRepository, Dispatchers.IO),
            getGroupListOnMonth = GetGroupListUseCaseImpl(groupRepository, Dispatchers.IO),
            getGroupWithClassDaysById = GetGroupWithClassDaysByIdUseCaseImpl(
                groupRepository,
                Dispatchers.IO
            ),
            getGroupWithPlayersById = GetGroupWithPlayersByIdUseCaseImpl(
                groupRepository,
                Dispatchers.IO
            ),
            getStatistics = GetStatisticsUseCaseImpl(groupRepository)
        )
    }
}
