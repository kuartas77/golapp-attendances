package com.golapp.attendances.ui.screens.groups.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golapp.attendances.di.IoDispatcher
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.usecases.groups.GroupsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groupsUseCases: GroupsUseCases,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val currentMonth = LocalDate.now().monthValue
    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState = _uiState.onSubscription { loadGroups() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = GroupsUiState(isLoading = true)
        )

    private fun loadGroups(query: String = "") {
        viewModelScope.launch(ioDispatcher) {
            groupsUseCases.observeGroupsWithClassDaysOnMonthUseCase(currentMonth)
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
                .collect { groups ->
                    if (groups.isEmpty()) {
                        _uiState.update { it.copy(isLoading = false) }
                    } else if (query.isNotEmpty()) {
                        val filteredGroups =
                            groups.filter { it.group.fullGroup.contains(query, ignoreCase = true) }
                        _uiState.update { it.copy(listGroups = filteredGroups, isLoading = false) }
                    } else {
                        _uiState.update { it.copy(listGroups = groups, isLoading = false) }
                    }
                }
        }
    }

    private fun getSelectedGroup(id: Int) {
        viewModelScope.launch(ioDispatcher) {
            _uiState.update {
                it.copy(
                    selectedGroup = it.listGroups.find { group -> group.group.id == id },
                    isLoading = true
                )
            }
        }
    }

    private fun syncGroups() {
        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isLoading = true) }
            groupsUseCases.syncAssignedGroupsUseCase()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onEvent(event: GroupsUiEvent) {
        when (event) {
            is GroupsUiEvent.OnSearchGroup -> loadGroups(event.query)
            is GroupsUiEvent.OnSelectGroup -> getSelectedGroup(event.item.group.id)
            GroupsUiEvent.SyncGroups -> syncGroups()
            GroupsUiEvent.OnClearText -> loadGroups()
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
    data object OnClearText : GroupsUiEvent
    data class OnSelectGroup(val item: GroupWithClassDays) : GroupsUiEvent
    data object SyncGroups : GroupsUiEvent
}
