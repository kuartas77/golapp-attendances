package com.golapp.attendances.feature.auth

import org.junit.Assert.assertEquals
import org.junit.Test

class AuthUiStateTest {

    @Test
    fun `production state never embeds credentials`() {
        val state = AuthUiState()

        assertEquals("", state.email)
        assertEquals("", state.password)
    }
}
