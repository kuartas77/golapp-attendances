package com.golapp.attendances.ui.screens.home

import androidx.lifecycle.ViewModel
import com.golapp.attendances.domain.repository.AuthRepository
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    suspend fun logout() {
        authRepository.logout()
    }


}
