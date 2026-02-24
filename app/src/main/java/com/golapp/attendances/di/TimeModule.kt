package com.golapp.attendances.di

import com.golapp.attendances.domain.time.SystemYearProvider
import com.golapp.attendances.domain.time.YearProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeModule {

    @Binds
    abstract fun bindYearProvider(impl: SystemYearProvider): YearProvider
}