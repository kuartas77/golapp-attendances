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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GroupsUseCasesModule {

    @Provides
    @Singleton
    fun provideGroupUseCases(groupRepository: GroupRepository): GroupUseCases {
        return GroupUseCases(
            syncGroups = SyncGroupsUseCaseImpl(groupRepository),
            getGroupListOnMonth = GetGroupListUseCaseImpl(groupRepository),
            getGroupWithClassDaysById = GetGroupWithClassDaysByIdUseCaseImpl(groupRepository),
            getGroupWithPlayersById = GetGroupWithPlayersByIdUseCaseImpl(groupRepository),
            getStatistics = GetStatisticsUseCaseImpl(groupRepository)
        )
    }
}
