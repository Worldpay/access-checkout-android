package com.worldpay.access.checkout.session.api
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.*

import org.junit.Test

class DefaultFactoryTest {

    private val factory = DefaultFactory()

    @Test
    fun `should allow to retrieve an instance of a LocalBroadcastManagerFactory`() {
        assertNotNull(factory.getLocalBroadcastManagerFactory(mock()))
    }

    @Test
    fun `should allow to retrieve an instance of a SessionRequestSender`() {
        assertNotNull(factory.getSessionRequestSender())
    }
}