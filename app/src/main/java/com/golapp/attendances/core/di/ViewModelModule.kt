package com.golapp.attendances.core.di

import com.golapp.attendances.feature.attendances.AttendancesUiState
import com.golapp.attendances.feature.auth.AuthUiState
import com.golapp.attendances.feature.groups.GroupsUiState
import com.golapp.attendances.feature.home.HomeUiState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @Provides
    fun provideAuthInitialState(): AuthUiState = AuthUiState(isLoading = true)

    @Provides
    fun provideHomeInitialState(): HomeUiState = HomeUiState(isLoading = true)

    @Provides
    fun provideGroupsInitialState(): GroupsUiState = GroupsUiState(isLoading = true)

    @Provides
    fun provideAttendancesInitialState(): AttendancesUiState =
        AttendancesUiState(isLoading = true)
}
