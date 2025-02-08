package com.golapp.attendances.ui.screens.attendances.di

import com.golapp.attendances.domain.repository.AttendanceRepository
import com.golapp.attendances.ui.screens.attendances.usecases.AttendancesUseCases
import com.golapp.attendances.ui.screens.attendances.usecases.GetAttendancesByClassDayUseCaseImpl
import com.golapp.attendances.ui.screens.attendances.usecases.GetClassDayByIdUseCaseImpl
import com.golapp.attendances.ui.screens.attendances.usecases.TakeAttendanceUseCaseImpl
import com.golapp.attendances.ui.screens.attendances.usecases.VerifyAttendancesByClassIdUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AttendancesModule {

    @Provides
    @Singleton
    fun provideAttendancesUseCases(
        attendanceRepository: AttendanceRepository
    ): AttendancesUseCases {
        return AttendancesUseCases(
            getClassDayById = GetClassDayByIdUseCaseImpl(attendanceRepository),
            getAttendancesByClassDay = GetAttendancesByClassDayUseCaseImpl(attendanceRepository),
            takeAttendance = TakeAttendanceUseCaseImpl(attendanceRepository),
            verifyAttendancesByClassId = VerifyAttendancesByClassIdUseCaseImpl(attendanceRepository)
        )
    }
}
