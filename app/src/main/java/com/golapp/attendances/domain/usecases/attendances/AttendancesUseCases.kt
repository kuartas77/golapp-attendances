package com.golapp.attendances.domain.usecases.attendances

import com.golapp.attendances.domain.usecases.classDays.GetClassDayByIdUseCase

data class AttendancesUseCases(
    val ensureAttendancesForClassDayUseCase: EnsureAttendancesForClassDayUseCase,
    val updateAttendanceValueUseCase: UpdateAttendanceValueUseCase,
    val syncAttendanceUseCase: SyncAttendanceUseCase,
    val getClassDayByIdUseCase: GetClassDayByIdUseCase,
    val getAttendancesByClassDayUseCase: GetAttendancesByClassDayUseCase,
    val takeAttendanceUseCase: TakeAttendanceUseCase
)
