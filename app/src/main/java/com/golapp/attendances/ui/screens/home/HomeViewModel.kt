package com.golapp.attendances.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golapp.attendances.domain.models.User
import com.golapp.attendances.domain.repository.AttendanceRepository
import com.golapp.attendances.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.onSubscription { getUserData() }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.Lazily,
        initialValue = HomeUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            attendanceRepository.syncAttendances()
        }
    }

    suspend fun logout() {
        authRepository.logout()
    }

    suspend fun getUserData() {
        authRepository.getUserData().collect { user ->
            _uiState.value = HomeUiState(user = user, isLoading = false)
        }
    }


}

data class HomeUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
