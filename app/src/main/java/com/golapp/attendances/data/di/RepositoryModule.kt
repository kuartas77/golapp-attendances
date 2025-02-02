package com.golapp.attendances.data.di

import com.golapp.attendances.data.local.datasources.AttendancesLocalDataSource
import com.golapp.attendances.data.local.datasources.ClassDayLocalDataSource
import com.golapp.attendances.data.local.datasources.GroupsLocalDataSource
import com.golapp.attendances.data.local.datasources.PlayersLocalDataSource
import com.golapp.attendances.data.local.datasources.StoreLocalDataSource
import com.golapp.attendances.data.remote.datasources.AttendancesRemoteDataSource
import com.golapp.attendances.data.remote.datasources.AuthRemoteDataSource
import com.golapp.attendances.data.remote.datasources.GroupsRemoteDataSource
import com.golapp.attendances.data.repository.AttendanceRepositoryImpl
import com.golapp.attendances.data.repository.AuthRepositoryImpl
import com.golapp.attendances.data.repository.GroupRepositoryImpl
import com.golapp.attendances.domain.repository.AttendanceRepository
import com.golapp.attendances.domain.repository.AuthRepository
import com.golapp.attendances.domain.repository.GroupRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideAttendanceRepository(
        attendanceLocalDataSource: AttendancesLocalDataSource,
        attendanceRemoteDataSource: AttendancesRemoteDataSource,
        classDayLocalDatasource: ClassDayLocalDataSource,
        groupLocalDataSource: GroupsLocalDataSource,

        ): AttendanceRepository {
        return AttendanceRepositoryImpl(
            attendanceLocalDataSource = attendanceLocalDataSource,
            attendanceRemoteDataSource = attendanceRemoteDataSource,
            classDayLocalDataSource = classDayLocalDatasource,
            groupLocalDataSource = groupLocalDataSource,
            ioDispatcher = Dispatchers.IO
        )
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authRemoteDatasource: AuthRemoteDataSource,
        storeLocalDatasource: StoreLocalDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(
            authRemoteDatasource,
            storeLocalDatasource,
            Dispatchers.IO
        )
    }

    @Provides
    @Singleton
    fun provideGroupRepository(
        groupsLocalDataSource: GroupsLocalDataSource,
        groupsRemoteDataSource: GroupsRemoteDataSource,
        attendanceLocalDataSource: AttendancesLocalDataSource,
        playerLocalDataSource: PlayersLocalDataSource,
        classDayLocalDatasource: ClassDayLocalDataSource
    ): GroupRepository {
        return GroupRepositoryImpl(
            groupsLocalDataSource,
            groupsRemoteDataSource,
            attendanceLocalDataSource,
            playerLocalDataSource,
            classDayLocalDatasource,
            Dispatchers.IO
        )
    }
}
