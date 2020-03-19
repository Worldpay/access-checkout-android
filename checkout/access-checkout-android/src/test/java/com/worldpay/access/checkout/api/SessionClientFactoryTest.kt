package com.worldpay.access.checkout.api

import org.junit.Test
import kotlin.test.assertNotNull

class SessionClientFactoryTest {
    @Test
    fun `should be able to create a session client`() {
        val sessionClientFactory = SessionClientFactory()
        val sessionClient = sessionClientFactory.createClient()
        assertNotNull(sessionClient)
    }
}
