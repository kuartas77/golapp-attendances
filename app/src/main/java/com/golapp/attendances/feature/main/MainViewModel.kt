package com.golapp.attendances.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golapp.attendances.domain.usecases.auth.AuthUseCases
import com.golapp.attendances.feature.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState
        .onSubscription {
            getUserData()
            checkLogin()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(isLoading = true)
        )

    fun getUserData() {
        viewModelScope.launch {
            val user = authUseCases.getUserDataUseCase()
            _uiState.value = HomeUiState(user = user, isLoading = false, isLoggedIn = true)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authUseCases.logoutUseCase()
            _uiState.update { it.copy(isLoading = false, isLoggedIn = false) }
        }
    }

    private fun checkLogin() {
        viewModelScope.launch {
            authUseCases.checkLoginUseCase().collect { isLoggedIn ->
                if (!isLoggedIn) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = false
                        )
                    }
                }
            }
        }
    }
}


