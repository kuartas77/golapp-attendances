package com.golapp.attendances.core.di

import androidx.work.WorkManager
import com.golapp.attendances.domain.repositories.AttendanceRepository
import com.golapp.attendances.domain.repositories.AuthRepository
import com.golapp.attendances.domain.repositories.ClassDayRepository
import com.golapp.attendances.domain.repositories.GroupRepository
import com.golapp.attendances.domain.time.YearProvider
import com.golapp.attendances.domain.usecases.attendances.AttendancesUseCases
import com.golapp.attendances.domain.usecases.attendances.EnsureAttendancesForClassDayUseCase
import com.golapp.attendances.domain.usecases.attendances.GetAttendanceStatisticsUseCase
import com.golapp.attendances.domain.usecases.attendances.GetAttendancesByClassDayUseCase
import com.golapp.attendances.domain.usecases.attendances.SyncAttendanceUseCase
import com.golapp.attendances.domain.usecases.attendances.TakeAttendanceUseCase
import com.golapp.attendances.domain.usecases.attendances.UpdateAttendanceValueUseCase
import com.golapp.attendances.domain.usecases.auth.AuthUseCases
import com.golapp.attendances.domain.usecases.auth.AuthenticateWithEmailUseCase
import com.golapp.attendances.domain.usecases.auth.CheckLoginUseCase
import com.golapp.attendances.domain.usecases.auth.GetUserDataUseCase
import com.golapp.attendances.domain.usecases.auth.LogoutUseCase
import com.golapp.attendances.domain.usecases.auth.ValidateEmailUseCase
import com.golapp.attendances.domain.usecases.auth.ValidatePasswordUseCase
import com.golapp.attendances.domain.usecases.auth.ValidateTokenExpiryUseCase
import com.golapp.attendances.domain.usecases.classDays.GetClassDayByIdUseCase
import com.golapp.attendances.domain.usecases.groups.GetGroupWhitPlayersByIdUseCase
import com.golapp.attendances.domain.usecases.groups.GroupsUseCases
import com.golapp.attendances.domain.usecases.groups.ObserveClassDaysByGroupUseCase
import com.golapp.attendances.domain.usecases.groups.ObserveGroupsUseCase
import com.golapp.attendances.domain.usecases.groups.ObserveGroupsWithClassDaysOnMonthUseCase
import com.golapp.attendances.domain.usecases.groups.SyncAssignedGroupsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCasesModule {

    @Provides
    @Singleton
    fun provideValidateEmailUseCase(): ValidateEmailUseCase = ValidateEmailUseCase()

    @Provides
    @Singleton
    fun provideValidatePasswordUseCase(): ValidatePasswordUseCase = ValidatePasswordUseCase()

    @Provides
    @Singleton
    fun provideAuthenticateWithEmailUseCase(
        authRepository: AuthRepository
    ): AuthenticateWithEmailUseCase = AuthenticateWithEmailUseCase(authRepository)

    @Provides
    @Singleton
    fun provideValidateTokenExpiryUseCase(
        authRepository: AuthRepository
    ): ValidateTokenExpiryUseCase = ValidateTokenExpiryUseCase(authRepository)

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        authRepository: AuthRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): LogoutUseCase = LogoutUseCase(authRepository, ioDispatcher)

    @Provides
    @Singleton
    fun provideGetUserDataUseCase(authRepository: AuthRepository) =
        GetUserDataUseCase(authRepository)

    @Provides
    @Singleton
    fun provideCheckLoginUseCase(authRepository: AuthRepository) = CheckLoginUseCase(authRepository)

    @Provides
    @Singleton
    fun provideAuthUseCases(
        validateEmail: ValidateEmailUseCase,
        validatePassword: ValidatePasswordUseCase,
        loginWithEmail: AuthenticateWithEmailUseCase,
        validateTokenExpiryUseCase: ValidateTokenExpiryUseCase,
        getUserDataUseCase: GetUserDataUseCase,
        checkLoginUseCase: CheckLoginUseCase,
        logoutUseCase: LogoutUseCase
    ): AuthUseCases = AuthUseCases(
        validateEmail,
        validatePassword,
        loginWithEmail,
        validateTokenExpiryUseCase,
        getUserDataUseCase,
        checkLoginUseCase,
        logoutUseCase
    )

    @Provides
    @Singleton
    fun provideSyncAttendanceUseCase(
        attendanceRepository: AttendanceRepository,
        workManager: WorkManager
    ): SyncAttendanceUseCase = SyncAttendanceUseCase(attendanceRepository, workManager)

    @Provides
    @Singleton
    fun provideEnsureAttendancesForClassDayUseCase(
        attendanceRepository: AttendanceRepository,
        groupRepository: GroupRepository,
        yearProvider: YearProvider
    ): EnsureAttendancesForClassDayUseCase =
        EnsureAttendancesForClassDayUseCase(attendanceRepository, groupRepository, yearProvider)

    @Provides
    @Singleton
    fun provideUpdateAttendanceValueUseCase(
        attendanceRepository: AttendanceRepository,
        workManager: WorkManager,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): UpdateAttendanceValueUseCase =
        UpdateAttendanceValueUseCase(attendanceRepository, workManager, ioDispatcher)

    @Provides
    @Singleton
    fun provideGetClassDayByIdUseCase(
        classDayRepository: ClassDayRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): GetClassDayByIdUseCase = GetClassDayByIdUseCase(
        classDayRepository,
        ioDispatcher
    )

    @Provides
    @Singleton
    fun provideGetAttendancesByClassDayUseCase(
        attendanceRepository: AttendanceRepository,
        yearProvider: YearProvider,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): GetAttendancesByClassDayUseCase = GetAttendancesByClassDayUseCase(
        attendanceRepository,
        yearProvider,
        ioDispatcher
    )

    @Provides
    @Singleton
    fun provideTakeAttendanceUseCase(
        attendanceRepository: AttendanceRepository
    ): TakeAttendanceUseCase = TakeAttendanceUseCase(attendanceRepository)

    @Provides
    @Singleton
    fun provideGetAttendanceStatisticsUseCase(
        attendanceRepository: AttendanceRepository
    ): GetAttendanceStatisticsUseCase = GetAttendanceStatisticsUseCase(attendanceRepository)


    @Provides
    @Singleton
    fun provideAttendancesUseCases(
        syncAttendanceUseCase: SyncAttendanceUseCase,
        ensureAttendancesForClassDayUseCase: EnsureAttendancesForClassDayUseCase,
        updateAttendanceValueUseCase: UpdateAttendanceValueUseCase,
        getClassDayByIdUseCase: GetClassDayByIdUseCase,
        getAttendancesByClassDayUseCase: GetAttendancesByClassDayUseCase,
        takeAttendanceUseCase: TakeAttendanceUseCase,
        getAttendanceStatisticsUseCase: GetAttendanceStatisticsUseCase
    ): AttendancesUseCases = AttendancesUseCases(
        ensureAttendancesForClassDayUseCase,
        updateAttendanceValueUseCase,
        syncAttendanceUseCase,
        getClassDayByIdUseCase,
        getAttendancesByClassDayUseCase,
        takeAttendanceUseCase,
        getAttendanceStatisticsUseCase
    )

    // Groups
    @Provides
    @Singleton
    fun provideGetGroupWhitPlayersByIdUseCase(
        groupRepository: GroupRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): GetGroupWhitPlayersByIdUseCase =
        GetGroupWhitPlayersByIdUseCase(groupRepository, ioDispatcher)

    @Provides
    @Singleton
    fun provideObserveClassDaysByGroupUseCase(
        groupRepository: GroupRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): ObserveClassDaysByGroupUseCase =
        ObserveClassDaysByGroupUseCase(groupRepository, ioDispatcher)

    @Provides
    @Singleton
    fun provideObserveGroupsUseCase(
        groupRepository: GroupRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): ObserveGroupsUseCase = ObserveGroupsUseCase(groupRepository, ioDispatcher)

    @Provides
    @Singleton
    fun provideObserveGroupsWithClassDaysOnMonthUseCase(
        groupRepository: GroupRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): ObserveGroupsWithClassDaysOnMonthUseCase =
        ObserveGroupsWithClassDaysOnMonthUseCase(groupRepository, ioDispatcher)

    @Provides
    @Singleton
    fun provideSyncAssignedGroupsUseCase(
        groupRepository: GroupRepository
    ): SyncAssignedGroupsUseCase = SyncAssignedGroupsUseCase(groupRepository)

    @Provides
    @Singleton
    fun provideGroupsUseCases(
        syncAssignedGroupsUseCase: SyncAssignedGroupsUseCase,
        observeGroupsWithClassDaysOnMonthUseCase: ObserveGroupsWithClassDaysOnMonthUseCase,
        observeGroupsUseCase: ObserveGroupsUseCase,
        observeClassDaysByGroupUseCase: ObserveClassDaysByGroupUseCase,
        getGroupWhitPlayersByIdUseCase: GetGroupWhitPlayersByIdUseCase,
    ): GroupsUseCases = GroupsUseCases(
        syncAssignedGroupsUseCase,
        observeGroupsWithClassDaysOnMonthUseCase,
        observeGroupsUseCase,
        observeClassDaysByGroupUseCase,
        getGroupWhitPlayersByIdUseCase
    )
}
