package com.worldpay.access.checkout.session.broadcast

import com.worldpay.access.checkout.client.SessionResponseListener
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.test.assertNotNull

class SessionBroadcastManagerFactoryTest {

    @Test
    fun `should instantiate a session broadcast manager`() {
        val localBroadcastManagerFactoryMock = mock(LocalBroadcastManagerFactory::class.java)
        val externalSessionResponseListenerMock = mock(SessionResponseListener::class.java)

        val sessionBroadcastManagerFactory = SessionBroadcastManagerFactory(
            localBroadcastManagerFactoryMock,
            externalSessionResponseListenerMock
        )

        assertNotNull(sessionBroadcastManagerFactory.createInstance())
    }

}