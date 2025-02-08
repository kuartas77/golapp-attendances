package com.golapp.attendances.ui.screens.attendances.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.golapp.attendances.common.di.IoDispatcher
import com.golapp.attendances.domain.models.Attendance
import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.ui.navigation.graphs.AttendanceGraph
import com.golapp.attendances.ui.screens.attendances.usecases.AttendancesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AttendancesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val attendancesUseCases: AttendancesUseCases,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val attendanceRoute: AttendanceGraph.Attendances = savedStateHandle.toRoute()
    private val _uiState = MutableStateFlow(AttendancesUiState())
    val uiState = _uiState.onSubscription { loadAttendances() }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = AttendancesUiState(isLoading = true)
    )

    /**
     *  1. cargar el classday de DB, para poder realizar la peticion de asistencias
     *  2. cargar el grupo del classday del mes actual
     *  3. cargar las asistencias de DB de ese classday
     *  4. si no hay asistencias en DB, cargarlas de la API
     *  5. si hay asistencias en la API, actualizarlas
     *  6. si no hay asistencias en la API crearlas en la DB con los players del grupo
     **/
    private fun loadAttendances() {
        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isLoading = true) }
            attendancesUseCases.getClassDayById(attendanceRoute.classDayId).let { classDay ->
                _uiState.update { it.copy(classDaySelected = classDay) }
                async {
                    attendancesUseCases.verifyAttendancesByClassId(classDay)

                    attendancesUseCases.getAttendancesByClassDay(classDay).collect { attendances ->
                        _uiState.update { it.copy(listAttendances = attendances, isLoading = false) }
                    }
                }.await()
            }
        }
    }

    private fun filterList(query: String) {
        _uiState.update { it.copy(query = query) }
        if (query.isNotBlank()) {
            _uiState.update {
                it.copy(
                    listAttendances = it.listAttendances.filter {
                        it.player.uniqueCode.contains(
                            query,
                            ignoreCase = true
                        )
                    }
                )
            }
        } else {
            _uiState.update {
                it.copy(listAttendances = it.listAttendances, isLoading = false)
            }
        }
    }

    private fun takeAttendance(attendanceWithPlayer: AttendanceWithPlayer) {

        val attendance = Attendance(
            id = attendanceWithPlayer.id,
            attendanceId = attendanceWithPlayer.attendanceId,
            schoolId = attendanceWithPlayer.schoolId,
            trainingGroupId = attendanceWithPlayer.trainingGroupId,
            inscriptionId = attendanceWithPlayer.inscriptionId,
            year = attendanceWithPlayer.year,
            month = attendanceWithPlayer.month,
            column = attendanceWithPlayer.column,
            value = attendanceWithPlayer.value,
            playerId = attendanceWithPlayer.playerId
        )

        viewModelScope.launch {
            val classDay = _uiState.value.classDaySelected
            attendancesUseCases.takeAttendance(attendance)
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
}
