package com.golapp.attendances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SplashViewModel: ViewModel() {
    private val mutableStateFlow = MutableStateFlow(true)
    val isLoading = mutableStateFlow
    init {
        viewModelScope.launch {
            delay(4000L)
            mutableStateFlow.value = false
        }
    }
}
