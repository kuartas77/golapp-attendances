package com.golapp.attendances.core.di

import com.golapp.attendances.core.FirebaseCrashReporter
import com.golapp.attendances.domain.CrashReporter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CrashReporterModule {

    @Binds
    @Singleton
    abstract fun bindCrashReporter(
        impl: FirebaseCrashReporter
    ): CrashReporter
}