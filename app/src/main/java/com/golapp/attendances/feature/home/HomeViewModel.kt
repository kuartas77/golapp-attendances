package com.golapp.attendances.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golapp.attendances.di.IoDispatcher
import com.golapp.attendances.domain.models.Statistics
import com.golapp.attendances.domain.models.User
import com.golapp.attendances.domain.usecases.attendances.AttendancesUseCases
import com.golapp.attendances.domain.usecases.auth.AuthUseCases
import com.golapp.attendances.domain.usecases.groups.GroupsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val attendanceUseCases: AttendancesUseCases,
    private val groupsUseCases: GroupsUseCases,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var currentDayJob: Job? = null
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.onSubscription {
        checkLogin()
        syncAttendances()
        fetchStatistics()
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(isLoading = true)
        )

    fun syncAttendances() {
        currentDayJob?.cancel()
        currentDayJob = viewModelScope.launch(ioDispatcher) {
            runCatching {
                attendanceUseCases.syncAttendanceUseCase()
                groupsUseCases.syncAssignedGroupsUseCase()
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun checkLogin() {
        viewModelScope.launch(ioDispatcher) {
            authUseCases.checkLoginUseCase().collect { isLoggedIn ->
                if (!isLoggedIn) {
                    _uiState.update {
                        it.copy(
                            isLoading = true,
                            isLoggedIn = false,
                            error = null
                        )
                    }
                    authUseCases.logoutUseCase()
                }
            }
        }
    }

    fun fetchStatistics() {
        viewModelScope.launch(ioDispatcher) {
            runCatching {
                attendanceUseCases.getAttendanceStatisticsUseCase()
            }.onSuccess { statistics ->
                _uiState.update { it.copy(listStatistics = statistics) }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
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
