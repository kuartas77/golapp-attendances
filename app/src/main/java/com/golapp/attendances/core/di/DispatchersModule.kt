package com.golapp.attendances.core.di

import com.golapp.attendances.core.coroutines.ReportingCoroutineExceptionHandler
import com.golapp.attendances.domain.CrashReporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @DefaultDispatcher
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @IoDispatcher
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @MainDispatcher
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    @ApplicationExceptionHandler
    fun providesApplicationExceptionHandler(
        crashReporter: CrashReporter,
    ): CoroutineExceptionHandler = ReportingCoroutineExceptionHandler(crashReporter)

    @Provides
    @Singleton
    @ApplicationScope
    fun providesCoroutineScope(
        @MainDispatcher mainDispatcher: CoroutineDispatcher,
        @ApplicationExceptionHandler exceptionHandler: CoroutineExceptionHandler,
    ): CoroutineScope = CoroutineScope(
        SupervisorJob() + mainDispatcher + exceptionHandler + CoroutineName("application")
    )
}