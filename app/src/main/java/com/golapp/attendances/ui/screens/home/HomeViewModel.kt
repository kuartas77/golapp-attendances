package com.golapp.attendances.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golapp.attendances.common.di.IoDispatcher
import com.golapp.attendances.domain.models.Statistics
import com.golapp.attendances.domain.models.User
import com.golapp.attendances.ui.screens.attendances.usecases.AttendancesUseCases
import com.golapp.attendances.ui.screens.groups.usecases.GroupUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val attendanceUseCases: AttendancesUseCases,
    private val groupUseCases: GroupUseCases,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var currentDayJob: Job? = null
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.onSubscription {
        syncAttendances()
        fetchStatistics()
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HomeUiState(isLoading = true)
        )

    fun syncAttendances() {
        currentDayJob?.cancel()
        currentDayJob = viewModelScope.launch(ioDispatcher) {
            attendanceUseCases.syncAttendance()
            groupUseCases.syncGroups()
        }
    }

    fun fetchStatistics() {
        viewModelScope.launch(ioDispatcher) {
            groupUseCases.getStatistics().collect {
                _uiState.value = _uiState.value.copy(listStatistics = it)
            }
        }
    }
}

data class HomeUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val listStatistics: List<Statistics> = emptyList()
)
