package com.golapp.attendances.common.events

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object EventBus {
    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()

    suspend fun send(event: UiEvent) = _events.send(event)
}

sealed interface UiEvent {
    data class ShowToast(val resource: UiText.StringResource) : UiEvent
    data class ShowSnackbar(val resource: UiText.StringResource) : UiEvent
}

sealed class UiText {
    data class DynamicString(val value: String) : UiText()

    data object Empty : UiText()

    class StringResource(
        @param:StringRes val resId: Int,
        vararg val args: Any
    ) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
            Empty -> ""
        }
    }
}