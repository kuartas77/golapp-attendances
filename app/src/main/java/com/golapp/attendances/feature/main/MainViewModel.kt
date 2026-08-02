package com.golapp.attendances.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golapp.attendances.domain.usecases.auth.AuthUseCases
import com.golapp.attendances.core.coroutines.rethrowIfCancellation
import com.golapp.attendances.feature.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    initialState: HomeUiState,
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()
    private var started = false
    private var logoutJob: Job? = null

    fun start() {
        if (started) return
        started = true
        viewModelScope.launch {
            try {
                authUseCases.checkLoginUseCase().collectLatest { isLoggedIn ->
                    if (isLoggedIn) {
                        val user = authUseCases.getUserDataUseCase()
                        _uiState.update {
                            it.copy(user = user, isLoading = false, isLoggedIn = true, error = null)
                        }
                    } else {
                        _uiState.update {
                            it.copy(user = null, isLoading = false, isLoggedIn = false, error = null)
                        }
                    }
                }
            } catch (error: Exception) {
                error.rethrowIfCancellation()
                _uiState.update {
                    it.copy(user = null, isLoading = false, isLoggedIn = false, error = error.message)
                }
            }
        }
    }

    fun logout() {
        if (logoutJob?.isActive == true) return
        logoutJob = viewModelScope.launch {
            try {
                authUseCases.logoutUseCase()
            } catch (error: Exception) {
                error.rethrowIfCancellation()
                _uiState.update { it.copy(error = error.message) }
            }
        }
    }

}


