package com.golapp.attendances.core.di

import com.golapp.attendances.data.repositories.AttendanceRepositoryImpl
import com.golapp.attendances.data.repositories.AuthRepositoryImpl
import com.golapp.attendances.data.repositories.ClassDayRepositoryImpl
import com.golapp.attendances.data.repositories.GroupRepositoryImpl
import com.golapp.attendances.domain.repositories.AttendanceRepository
import com.golapp.attendances.domain.repositories.AuthRepository
import com.golapp.attendances.domain.repositories.ClassDayRepository
import com.golapp.attendances.domain.repositories.GroupRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGroupRepository(impl: GroupRepositoryImpl): GroupRepository

    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(impl: AttendanceRepositoryImpl): AttendanceRepository

    @Binds
    @Singleton
    abstract fun bindClassDayRepository(impl: ClassDayRepositoryImpl): ClassDayRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

}