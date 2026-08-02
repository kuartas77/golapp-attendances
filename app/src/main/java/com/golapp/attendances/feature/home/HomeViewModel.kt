package com.golapp.attendances.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golapp.attendances.core.di.IoDispatcher
import com.golapp.attendances.core.coroutines.rethrowIfCancellation
import com.golapp.attendances.domain.models.Statistics
import com.golapp.attendances.domain.models.User
import com.golapp.attendances.domain.usecases.attendances.AttendancesUseCases
import com.golapp.attendances.domain.usecases.groups.GroupsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val attendanceUseCases: AttendancesUseCases,
    private val groupsUseCases: GroupsUseCases,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    initialState: HomeUiState,
) : ViewModel() {

    private var currentDayJob: Job? = null
    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()
    private var started = false

    fun start() {
        if (started) return
        started = true
        syncAttendances()
        fetchStatistics()
    }

    fun syncAttendances() {
        currentDayJob?.cancel()
        currentDayJob = viewModelScope.launch(ioDispatcher) {
            try {
                attendanceUseCases.syncAttendanceUseCase()
                groupsUseCases.syncAssignedGroupsUseCase()
            } catch (error: Exception) {
                error.rethrowIfCancellation()
                _uiState.update { it.copy(error = error.message) }
            }
        }
    }

    fun fetchStatistics() {
        viewModelScope.launch(ioDispatcher) {
            try {
                val statistics = attendanceUseCases.getAttendanceStatisticsUseCase()
                _uiState.update { it.copy(listStatistics = statistics) }
            } catch (error: Exception) {
                error.rethrowIfCancellation()
                _uiState.update { it.copy(error = error.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class HomeUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = true,
    val error: String? = null,
    val listStatistics: List<Statistics> = emptyList()
)
