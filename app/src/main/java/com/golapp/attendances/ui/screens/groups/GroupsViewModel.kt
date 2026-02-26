package com.golapp.attendances.ui.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golapp.attendances.di.IoDispatcher
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.usecases.groups.GroupsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groupsUseCases: GroupsUseCases,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val currentMonth = LocalDate.now().monthValue

    // ---- State holders ----
    private val queryFlow = MutableStateFlow("")
    private val selectedGroupIdFlow = MutableStateFlow<Int?>(null)
    private val isSyncingFlow = MutableStateFlow(false)

    // ---- One-shot effects (snackbar, navigation, etc.) ----
    private val _effects = MutableSharedFlow<GroupsUiEffect>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val effects: SharedFlow<GroupsUiEffect> = _effects.asSharedFlow()

    private val groupsResultFlow: StateFlow<GroupsResult> =
        groupsUseCases.observeGroupsWithClassDaysOnMonthUseCase(currentMonth)
            .map<List<GroupWithClassDays>, GroupsResult> { GroupsResult.Success(it) }
            .onStart { emit(GroupsResult.Loading) }
            .catch { e ->
                emit(GroupsResult.Error(e.message ?: "Error cargando grupos"))
            }
            .onEach { result ->
                // Error del stream (DB/observe) como snackbar one-shot
                if (result is GroupsResult.Error) {
                    _effects.tryEmit(GroupsUiEffect.ShowSnackbar(result.message))
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = GroupsResult.Loading
            )

    @OptIn(FlowPreview::class)
    val uiState: StateFlow<GroupsUiState> =
        combine(
            groupsResultFlow,
            queryFlow
                .map { it.trim() }
                .debounce(250)
                .distinctUntilChanged(),
            selectedGroupIdFlow,
            isSyncingFlow
        ) { result, query, selectedId, isSyncing ->

            val (isLoading, groups, blockingError) = when (result) {
                GroupsResult.Loading -> Triple(true, emptyList(), null)
                is GroupsResult.Success -> Triple(false, result.data, null)
                is GroupsResult.Error -> Triple(
                    false,
                    emptyList(),
                    result.message
                ) // útil para pantalla vacía con retry
            }

            val filtered = if (query.isBlank()) groups
            else groups.filter { it.group.fullGroup.contains(query, ignoreCase = true) }

            val selected = selectedId?.let { id -> groups.find { it.group.id == id } }

            GroupsUiState(
                query = query,
                isLoading = isLoading,
                isSyncing = isSyncing,
                listGroups = filtered,
                selectedGroup = selected,
                blockingError = blockingError
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GroupsUiState(isLoading = true)
        )

    fun onEvent(event: GroupsUiEvent) {
        when (event) {
            is GroupsUiEvent.OnSearchGroup -> queryFlow.value = event.query
            GroupsUiEvent.OnClearText -> queryFlow.value = ""
            is GroupsUiEvent.OnSelectGroup -> selectedGroupIdFlow.value = event.item.group.id
            GroupsUiEvent.SyncGroups -> syncGroups()
            GroupsUiEvent.Retry -> syncGroups()
        }
    }

    private fun syncGroups() {
        viewModelScope.launch(ioDispatcher) {
            if (isSyncingFlow.value) return@launch

            isSyncingFlow.value = true

            runCatching {
                groupsUseCases.syncAssignedGroupsUseCase()
            }.onFailure { e ->
                _effects.emit(
                    GroupsUiEffect.ShowSnackbar(
                        message = e.message ?: "Error sincronizando grupos",
                        actionLabel = "Reintentar",
                        action = GroupsUiAction.RetrySync
                    )
                )
            }

            isSyncingFlow.value = false
        }
    }

    // ---- Result wrapper ----
    private sealed interface GroupsResult {
        data object Loading : GroupsResult
        data class Success(val data: List<GroupWithClassDays>) : GroupsResult
        data class Error(val message: String) : GroupsResult
    }
}

data class GroupsUiState(
    val query: String = "",
    val isLoading: Boolean = false,      // carga inicial (skeleton)
    val isSyncing: Boolean = false,      // refresh/sync (pull-to-refresh)
    val listGroups: List<GroupWithClassDays> = emptyList(),
    val selectedGroup: GroupWithClassDays? = null,
    val blockingError: String? = null    // error “de pantalla” (por ejemplo cuando no hay data)
)

sealed interface GroupsUiEvent {
    data class OnSearchGroup(val query: String) : GroupsUiEvent
    data object OnClearText : GroupsUiEvent
    data class OnSelectGroup(val item: GroupWithClassDays) : GroupsUiEvent
    data object SyncGroups : GroupsUiEvent
    data object Retry : GroupsUiEvent
}

sealed interface GroupsUiEffect {
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val action: GroupsUiAction? = null
    ) : GroupsUiEffect
}

sealed interface GroupsUiAction {
    data object RetrySync : GroupsUiAction
}