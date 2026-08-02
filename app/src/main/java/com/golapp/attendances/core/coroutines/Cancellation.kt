package com.golapp.attendances.core.coroutines

import kotlin.coroutines.cancellation.CancellationException

fun Throwable.rethrowIfCancellation() {
    if (this is CancellationException) throw this
}