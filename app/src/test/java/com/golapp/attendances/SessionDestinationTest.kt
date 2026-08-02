package com.golapp.attendances

import com.golapp.attendances.core.navigation.graphs.Authentication
import com.golapp.attendances.core.navigation.graphs.Home
import org.junit.Assert.assertEquals
import org.junit.Test

class SessionDestinationTest {

    @Test
    fun `authenticated startup begins at home`() {
        assertEquals(Home, sessionDestination(isLoggedIn = true))
    }

    @Test
    fun `anonymous startup begins at authentication`() {
        assertEquals(Authentication, sessionDestination(isLoggedIn = false))
    }
}
