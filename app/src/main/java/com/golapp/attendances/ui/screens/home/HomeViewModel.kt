package com.golapp.attendances.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golapp.attendances.domain.models.User
import com.golapp.attendances.domain.repository.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private var currentDayJob: Job? = null

    init {
        syncAttendances()
    }

    fun syncAttendances() {
        currentDayJob?.cancel()
        currentDayJob = viewModelScope.launch {
            attendanceRepository.syncAttendances()
        }
    }
}

data class HomeUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)
