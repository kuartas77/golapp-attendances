package com.golapp.attendances.data.di

import android.content.Context
import androidx.work.WorkManager
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
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideAttendanceRepository(
        attendanceLocalDataSource: AttendancesLocalDataSource,
        attendanceRemoteDataSource: AttendancesRemoteDataSource,
        classDayLocalDatasource: ClassDayLocalDataSource,
        groupLocalDataSource: GroupsLocalDataSource,
        workManager: WorkManager
    ): AttendanceRepository {
        return AttendanceRepositoryImpl(
            attendanceLocalDataSource = attendanceLocalDataSource,
            attendanceRemoteDataSource = attendanceRemoteDataSource,
            classDayLocalDataSource = classDayLocalDatasource,
            groupLocalDataSource = groupLocalDataSource,
            workManager = workManager
        )
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authRemoteDatasource: AuthRemoteDataSource,
        storeLocalDatasource: StoreLocalDataSource,
        attendancesLocalDataSource: GroupsLocalDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(
            authRemoteDatasource,
            storeLocalDatasource,
            attendancesLocalDataSource,
            Dispatchers.IO
        )
    }

    @Provides
    @Singleton
    fun provideGroupRepository(
        groupsLocalDataSource: GroupsLocalDataSource,
        groupsRemoteDataSource: GroupsRemoteDataSource,
        playerLocalDataSource: PlayersLocalDataSource,
        classDayLocalDatasource: ClassDayLocalDataSource,
    ): GroupRepository {
        return GroupRepositoryImpl(
            groupsLocalDataSource,
            groupsRemoteDataSource,
            playerLocalDataSource,
            classDayLocalDatasource,
            Dispatchers.IO
        )
    }
}
