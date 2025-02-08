package com.golapp.attendances.ui.screens.attendances.usecases

import com.golapp.attendances.domain.usecases.attendances.GetAttendancesByClassDayUseCase
import com.golapp.attendances.domain.usecases.attendances.GetClassDayByIdUseCase
import com.golapp.attendances.domain.usecases.attendances.TakeAttendanceUseCase
import com.golapp.attendances.domain.usecases.attendances.VerifyAttendancesByClassIdUseCase
import javax.inject.Inject

data class AttendancesUseCases @Inject constructor(
    val getClassDayById: GetClassDayByIdUseCase,
    val getAttendancesByClassDay: GetAttendancesByClassDayUseCase,
    val takeAttendance: TakeAttendanceUseCase,
    val verifyAttendancesByClassId: VerifyAttendancesByClassIdUseCase
)
