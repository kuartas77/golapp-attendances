package com.golapp.attendances.core.coroutines

import com.golapp.attendances.domain.CrashReporter
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

class ReportingCoroutineExceptionHandler(
    private val crashReporter: CrashReporter,
) : AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        if (exception is CancellationException) return

        val operation = context[CoroutineName]?.name ?: "unnamed"
        try {
            crashReporter.log("Uncaught coroutine failure: $operation")
            crashReporter.setCustomKey("coroutine_operation", operation)
            crashReporter.recordException(exception)
        } catch (_: Exception) {
            // An observability failure must never recursively crash the handler.
        }
    }
}
