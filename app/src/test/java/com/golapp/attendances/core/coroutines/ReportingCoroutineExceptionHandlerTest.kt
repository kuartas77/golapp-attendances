package com.golapp.attendances.core.coroutines

import com.golapp.attendances.domain.CrashReporter
import kotlinx.coroutines.CoroutineName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CancellationException

class ReportingCoroutineExceptionHandlerTest {

    @Test
    fun `uncaught failure includes operation metadata`() {
        val reporter = FakeCrashReporter()
        val error = IllegalStateException("boom")

        ReportingCoroutineExceptionHandler(reporter)
            .handleException(CoroutineName("startup-session"), error)

        assertEquals("startup-session", reporter.keys["coroutine_operation"])
        assertEquals(error, reporter.recorded)
        assertTrue(reporter.logs.single().contains("startup-session"))
    }

    @Test
    fun `cancellation is not reported`() {
        val reporter = FakeCrashReporter()

        ReportingCoroutineExceptionHandler(reporter)
            .handleException(CoroutineName("cancelled"), CancellationException())

        assertTrue(reporter.logs.isEmpty())
        assertTrue(reporter.keys.isEmpty())
        assertEquals(null, reporter.recorded)
    }

    @Test
    fun `reporter failure does not escape handler`() {
        val reporter = FakeCrashReporter(shouldFail = true)

        ReportingCoroutineExceptionHandler(reporter)
            .handleException(CoroutineName("safe-handler"), IllegalStateException())
    }

    private class FakeCrashReporter(
        private val shouldFail: Boolean = false,
    ) : CrashReporter {
        val keys = mutableMapOf<String, String>()
        val logs = mutableListOf<String>()
        var recorded: Throwable? = null

        override fun setCustomKey(key: String, value: String) {
            if (shouldFail) error("reporter unavailable")
            keys[key] = value
        }

        override fun log(message: String) {
            if (shouldFail) error("reporter unavailable")
            logs += message
        }

        override fun recordException(t: Throwable) {
            if (shouldFail) error("reporter unavailable")
            recorded = t
        }
    }
}
