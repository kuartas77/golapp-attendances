package com.golapp.attendances.ui.screens.groups.usecases

import com.golapp.attendances.domain.usecases.groups.GetGroupListUseCase
import com.golapp.attendances.domain.usecases.groups.GetGroupWithClassDaysByIdUseCase
import com.golapp.attendances.domain.usecases.groups.GetGroupWithPlayersByIdUseCase
import com.golapp.attendances.domain.usecases.groups.SyncGroupsUseCase
import javax.inject.Inject

data class GroupUseCases @Inject constructor(
    val syncGroups: SyncGroupsUseCase,
    val getGroupList: GetGroupListUseCase,
    val getGroupWithClassDaysById: GetGroupWithClassDaysByIdUseCase,
    val getGroupWithPlayersById: GetGroupWithPlayersByIdUseCase
)
