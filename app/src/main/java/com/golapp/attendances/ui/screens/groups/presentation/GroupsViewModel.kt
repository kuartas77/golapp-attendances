package com.golapp.attendances.ui.screens.groups.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golapp.attendances.R
import com.golapp.attendances.common.ui.events.UiEvent
import com.golapp.attendances.common.ui.events.UiText
import com.golapp.attendances.common.ui.events.sendEvent
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.ui.screens.groups.usecases.GroupUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groupUseCases: GroupUseCases
) : ViewModel() {

    private val currentMonth = LocalDate.now().monthValue
    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState = _uiState.onSubscription { loadGroups() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GroupsUiState(isLoading = true)
        )

    private fun loadGroups(query: String = "") {
        viewModelScope.launch {
            _uiState.update { it.copy(query = query, isLoading = true) }
            groupUseCases.getGroupListOnMonth(month = currentMonth).collect { groups ->
                if (groups.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false) }
                    sendEvent(UiEvent.ShowSnackbar(UiText.StringResource(R.string.no_groups_found)))
                } else if (query.isNotEmpty()) {
                    val filteredGroups =
                        groups.filter { it.name.contains(query, ignoreCase = true) }
                    _uiState.update { it.copy(listGroups = filteredGroups, isLoading = false) }
                } else {
                    _uiState.update { it.copy(listGroups = groups, isLoading = false) }
                }
            }
        }
    }

    private fun getSelectedGroup(id: Int) {
        viewModelScope.launch {
            groupUseCases.getGroupWithClassDaysById(id).collect { group ->
                _uiState.update { it.copy(selectedGroup = group) }
            }
        }
    }

    private fun syncGroups() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            groupUseCases.syncGroups().collect { groups ->
                if (groups.isEmpty()) {
                    sendEvent(UiEvent.ShowSnackbar(UiText.StringResource(R.string.no_groups_found)))
                }
                _uiState.update { it.copy(listGroups = groups, isLoading = false) }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onEvent(event: GroupsUiEvent) {
        when (event) {
            is GroupsUiEvent.OnSearchGroup -> loadGroups(event.query)
            is GroupsUiEvent.OnSelectGroup -> getSelectedGroup(event.group.id)
            GroupsUiEvent.SyncGroups -> syncGroups()
        }
    }
}

data class GroupsUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val listGroups: List<GroupWithClassDays> = emptyList(),
    val selectedGroup: GroupWithClassDays? = null,
    val error: String? = null
)

sealed interface GroupsUiEvent {
    data class OnSearchGroup(val query: String) : GroupsUiEvent
    data class OnSelectGroup(val group: GroupWithClassDays) : GroupsUiEvent
    data object SyncGroups : GroupsUiEvent
}
