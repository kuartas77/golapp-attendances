package com.golapp.attendances.core.update

import com.google.android.play.core.install.model.UpdateAvailability
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ImmediateUpdatePolicyTest {
    @Test
    fun `starts only when an immediate update is available and allowed`() {
        assertTrue(
            ImmediateUpdatePolicy.shouldStart(UpdateAvailability.UPDATE_AVAILABLE, true)
        )
        assertFalse(
            ImmediateUpdatePolicy.shouldStart(UpdateAvailability.UPDATE_AVAILABLE, false)
        )
        assertFalse(
            ImmediateUpdatePolicy.shouldStart(UpdateAvailability.UPDATE_NOT_AVAILABLE, true)
        )
    }

    @Test
    fun `resumes only a developer triggered update in progress`() {
        assertTrue(
            ImmediateUpdatePolicy.shouldResume(
                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            )
        )
        assertFalse(ImmediateUpdatePolicy.shouldResume(UpdateAvailability.UPDATE_AVAILABLE))
    }
}
