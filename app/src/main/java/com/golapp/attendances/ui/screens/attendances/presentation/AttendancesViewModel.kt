package com.golapp.attendances.ui.screens.attendances.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.golapp.attendances.di.IoDispatcher
import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.domain.usecases.attendances.AttendancesUseCases
import com.golapp.attendances.ui.navigation.graphs.Attendances
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendancesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val attendancesUseCases: AttendancesUseCases,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val route: Attendances = savedStateHandle.toRoute()

    private val queryFlow = MutableStateFlow("")
    private val selectedKeyFlow = MutableStateFlow<String?>(null) // key estable (ej: uniqueCode)
    private val isSyncingFlow = MutableStateFlow(false)

    private val _effects = MutableSharedFlow<AttendancesUiEffect>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val effects: SharedFlow<AttendancesUiEffect> = _effects.asSharedFlow()

    private val classDayResultFlow: StateFlow<ClassDayResult> =
        flow<ClassDayResult> {
            val classDay = attendancesUseCases.getClassDayByIdUseCase(route.classDayId)
            emit(ClassDayResult.Success(classDay))
        }
            .flowOn(ioDispatcher)
            .onStart { emit(ClassDayResult.Loading) }
            .catch { e ->
                emit(ClassDayResult.Error(e.message ?: "Error cargando el día de clase"))
            }
            .onEach { result ->
                if (result is ClassDayResult.Error) {
                    _effects.tryEmit(
                        AttendancesUiEffect.ShowSnackbar(result.message)
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ClassDayResult.Loading
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val attendancesResultFlow: StateFlow<AttendancesResult> =
        classDayResultFlow
            .filterIsInstance<ClassDayResult.Success>()
            .map { it.data }
            .flatMapLatest { classDay ->
                attendancesUseCases.getAttendancesByClassDayUseCase(classDay)
                    .map<List<AttendanceWithPlayer>, AttendancesResult> {
                        AttendancesResult.Success(
                            it
                        )
                    }
                    .onStart {
                        // asegura la data 1 sola vez por classDay
                        runCatching {
                            attendancesUseCases.ensureAttendancesForClassDayUseCase(
                                classDay
                            )
                        }
                            .onFailure { e ->
                                _effects.tryEmit(
                                    AttendancesUiEffect.ShowSnackbar(
                                        message = e.message ?: "Error preparando asistencias",
                                        actionLabel = "Reintentar",
                                        action = AttendancesUiAction.RetryLoad
                                    )
                                )
                            }
                        emit(AttendancesResult.Loading)
                    }
                    .catch { e ->
                        emit(AttendancesResult.Error(e.message ?: "Error cargando asistencias"))
                    }
            }
            .onEach { result ->
                if (result is AttendancesResult.Error) {
                    _effects.tryEmit(
                        AttendancesUiEffect.ShowSnackbar(
                            message = result.message,
                            actionLabel = "Reintentar",
                            action = AttendancesUiAction.RetryLoad
                        )
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AttendancesResult.Loading
            )

    @OptIn(FlowPreview::class)
    val uiState: StateFlow<AttendancesUiState> =
        combine(
            classDayResultFlow,
            attendancesResultFlow,
            queryFlow
                .map { it.trim() }
                .debounce(250)
                .distinctUntilChanged(),
            selectedKeyFlow,
            isSyncingFlow
        ) { classDayResult, attendancesResult, query, selectedKey, isSyncing ->

            val classDay = (classDayResult as? ClassDayResult.Success)?.data

            val (isLoadingAttendances, allAttendances, blockingError) = when (attendancesResult) {
                AttendancesResult.Loading -> Triple(true, emptyList(), null)
                is AttendancesResult.Success -> Triple(false, attendancesResult.data, null)
                is AttendancesResult.Error -> Triple(false, emptyList(), attendancesResult.message)
            }

            val isLoadingClassDay = classDayResult is ClassDayResult.Loading
            val isLoading = isLoadingClassDay || isLoadingAttendances

            val filtered = if (query.isBlank()) {
                allAttendances
            } else {
                allAttendances.filter { a ->
                    a.player.uniqueCode.contains(query, ignoreCase = true) ||
                            a.player.fullNames.contains(query, ignoreCase = true)
                }
            }

            val selected = selectedKey?.let { key ->
                allAttendances.find { it.player.uniqueCode == key }
            }

            AttendancesUiState(
                query = query,
                classDaySelected = classDay,
                listAttendances = filtered,
                selectedAttendance = selected,
                isLoading = isLoading,
                isSyncing = isSyncing,
                blockingError = blockingError
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AttendancesUiState(isLoading = true)
        )

    fun onEvent(event: AttendancesUiEvent) {
        when (event) {
            is AttendancesUiEvent.OnSearchAttendance -> queryFlow.value = event.query
            AttendancesUiEvent.OnClearText -> queryFlow.value = ""

            is AttendancesUiEvent.OnSelectAttendance -> {
                selectedKeyFlow.value = event.attendanceWithPlayer.player.uniqueCode
            }

            is AttendancesUiEvent.OnTakeAttendance -> takeAttendance(event.attendanceWithPlayer)

            AttendancesUiEvent.SyncAttendances -> syncAttendances()
            AttendancesUiEvent.RetryLoad -> retryLoad()
        }
    }

    private fun retryLoad() {
        // Fuerza un “refresh” lógico: reemite query para recombinar y/o dispara ensure + flujo
        queryFlow.value = queryFlow.value
    }

    private fun takeAttendance(attendanceWithPlayer: AttendanceWithPlayer) {
        viewModelScope.launch(ioDispatcher) {
            runCatching {
                // Si tienes el use case:
                // attendancesUseCases.takeAttendanceUseCase(attendanceWithPlayer)
            }.onFailure { e ->
                _effects.emit(
                    AttendancesUiEffect.ShowSnackbar(
                        message = e.message ?: "Error guardando asistencia",
                        actionLabel = "Reintentar",
                        action = AttendancesUiAction.RetryTake(attendanceWithPlayer)
                    )
                )
            }
        }
    }

    private fun syncAttendances() {
        viewModelScope.launch(ioDispatcher) {
            if (isSyncingFlow.value) return@launch
            isSyncingFlow.value = true

            runCatching {
                attendancesUseCases.syncAttendanceUseCase()
            }.onFailure { e ->
                _effects.emit(
                    AttendancesUiEffect.ShowSnackbar(
                        message = e.message ?: "Error sincronizando asistencias",
                        actionLabel = "Reintentar",
                        action = AttendancesUiAction.RetrySync
                    )
                )
            }

            isSyncingFlow.value = false
        }
    }

    private sealed interface ClassDayResult {
        data object Loading : ClassDayResult
        data class Success(val data: ClassDay) : ClassDayResult
        data class Error(val message: String) : ClassDayResult
    }

    private sealed interface AttendancesResult {
        data object Loading : AttendancesResult
        data class Success(val data: List<AttendanceWithPlayer>) : AttendancesResult
        data class Error(val message: String) : AttendancesResult
    }
}

data class AttendancesUiState(
    val query: String = "",
    val classDaySelected: ClassDay? = null,
    val listAttendances: List<AttendanceWithPlayer> = emptyList(),
    val selectedAttendance: AttendanceWithPlayer? = null,
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val blockingError: String? = null
)

sealed interface AttendancesUiEvent {
    data class OnSearchAttendance(val query: String = "") : AttendancesUiEvent
    data object OnClearText : AttendancesUiEvent
    data class OnSelectAttendance(val attendanceWithPlayer: AttendanceWithPlayer) :
        AttendancesUiEvent

    data class OnTakeAttendance(val attendanceWithPlayer: AttendanceWithPlayer) : AttendancesUiEvent
    data object SyncAttendances : AttendancesUiEvent
    data object RetryLoad : AttendancesUiEvent
}

sealed interface AttendancesUiEffect {
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val action: AttendancesUiAction? = null
    ) : AttendancesUiEffect
}

sealed interface AttendancesUiAction {
    data object RetrySync : AttendancesUiAction
    data object RetryLoad : AttendancesUiAction
    data class RetryTake(val attendance: AttendanceWithPlayer) : AttendancesUiAction
}