package com.golapp.attendances.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val mutableStateFlow = MutableStateFlow(true)
    val isLoading = mutableStateFlow

    init {
        viewModelScope.launch {
            delay(SPLASH_MIN_DURATION_MS)
            mutableStateFlow.value = false
        }
    }

    private companion object {
        const val SPLASH_MIN_DURATION_MS = 700L
    }
}
