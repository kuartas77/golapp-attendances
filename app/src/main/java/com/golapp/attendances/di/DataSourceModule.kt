package com.golapp.attendances.di

import com.golapp.attendances.data.datasources.AttendanceRemoteDataSource
import com.golapp.attendances.data.datasources.AttendanceRemoteDataSourceImpl
import com.golapp.attendances.data.datasources.AuthRemoteDataSource
import com.golapp.attendances.data.datasources.AuthRemoteDataSourceImpl
import com.golapp.attendances.data.datasources.GroupRemoteDataSource
import com.golapp.attendances.data.datasources.GroupRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindGroupRemoteDataSource(impl: GroupRemoteDataSourceImpl): GroupRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAttendanceRemoteDataSource(impl: AttendanceRemoteDataSourceImpl): AttendanceRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(impl: AuthRemoteDataSourceImpl): AuthRemoteDataSource

}