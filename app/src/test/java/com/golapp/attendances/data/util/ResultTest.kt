package com.golapp.attendances.data.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CancellationException

class ResultTest {

    @Test(expected = CancellationException::class)
    fun `resultOf never converts cancellation into business failure`() {
        Unit.resultOf<Unit, Unit> { throw CancellationException("cancelled") }
    }

    @Test
    fun `resultOf keeps expected operation failure`() {
        val result = Unit.resultOf<Unit, Unit> { error("failed") }

        assertTrue(result.isFailure)
        assertEquals("failed", result.exceptionOrNull()?.message)
    }
}
