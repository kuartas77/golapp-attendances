package com.golapp.attendances.ui.screens.attendances.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.golapp.attendances.common.di.IoDispatcher
import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.ui.navigation.graphs.Attendances
import com.golapp.attendances.ui.screens.attendances.usecases.AttendancesUseCases
import com.golapp.attendances.ui.screens.groups.usecases.GroupUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendancesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val attendancesUseCases: AttendancesUseCases,
    private val groupUseCases: GroupUseCases,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val attendanceRoute: Attendances = savedStateHandle.toRoute()
    private val _uiState = MutableStateFlow(AttendancesUiState())
    val uiState = _uiState.onSubscription { loadAttendances() }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = AttendancesUiState(isLoading = true)
    )

    private fun loadAttendances() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val classDay = attendancesUseCases.getClassDayById(attendanceRoute.classDayId)
            _uiState.update { it.copy(classDaySelected = classDay) }

            async {
                attendancesUseCases.verifyAttendancesByClassId(classDay)
                attendancesUseCases.getAttendancesByClassDay(classDay).collect { attendances ->
                    _uiState.update {
                        it.copy(listAttendances = attendances, isLoading = false)
                    }
                }
            }.await()
        }
    }

    private fun filterList(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val classDay = _uiState.value.classDaySelected
            if (classDay != null) {
                attendancesUseCases.getAttendancesByClassDay(classDay).collect { attendances ->
                    if (query.isBlank() || query.isEmpty()) {
                        _uiState.update {
                            it.copy(listAttendances = attendances, isLoading = false)
                        }
                    } else {
                        val filteredCode = attendances.filter {
                            it.player.uniqueCode.contains(
                                query,
                                ignoreCase = true
                            )
                        }
                        val filteredName = attendances.filter {
                            it.player.fullNames.contains(
                                query,
                                ignoreCase = true
                            )
                        }

                        val filtered = filteredCode.plus(filteredName)

                        _uiState.update { it.copy(listAttendances = filtered, isLoading = false) }
                    }

                }
            }
        }
    }

    private fun takeAttendance(attendanceWithPlayer: AttendanceWithPlayer) {
        viewModelScope.launch {
            attendancesUseCases.takeAttendance(attendanceWithPlayer)
        }
    }

    private fun syncAttendances() {
        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isLoading = true) }
            attendancesUseCases.syncAttendance()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onEvent(event: AttendancesUiEvent) {
        when (event) {
            is AttendancesUiEvent.OnSearchAttendance -> filterList(event.query)
            is AttendancesUiEvent.OnTakeAttendance -> {
                takeAttendance(event.attendanceWithPlayer)
            }

            is AttendancesUiEvent.OnSelectAttendance -> {
                _uiState.update { it.copy(selectedAttendance = event.attendanceWithPlayer) }
            }

            AttendancesUiEvent.SyncAttendances -> syncAttendances()
        }
    }
}

data class AttendancesUiState(
    val query: String = "",
    val classDaySelected: ClassDay? = null,
    val listAttendances: List<AttendanceWithPlayer> = emptyList(),
    val selectedAttendance: AttendanceWithPlayer? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface AttendancesUiEvent {
    data class OnSearchAttendance(val query: String = "") : AttendancesUiEvent
    data class OnSelectAttendance(val attendanceWithPlayer: AttendanceWithPlayer) :
        AttendancesUiEvent

    data class OnTakeAttendance(val attendanceWithPlayer: AttendanceWithPlayer) : AttendancesUiEvent
    data object SyncAttendances : AttendancesUiEvent
}
