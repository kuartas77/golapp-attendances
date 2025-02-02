package com.golapp.attendances.common.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

fun ViewModel.sendEvent(event: UiEvent) {
    viewModelScope.launch {
        EventBus.send(event)
    }
}
