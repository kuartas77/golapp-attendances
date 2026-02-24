package com.golapp.attendances.domain.usecases.groups

data class GroupsUseCases(
    val syncAssignedGroupsUseCase: SyncAssignedGroupsUseCase,
    val observeGroupsWithClassDaysOnMonthUseCase: ObserveGroupsWithClassDaysOnMonthUseCase,
    val observeGroupsUseCase: ObserveGroupsUseCase,
    val observeClassDaysByGroupUseCase: ObserveClassDaysByGroupUseCase,
    val getGroupWhitPlayersByIdUseCase: GetGroupWhitPlayersByIdUseCase,
)