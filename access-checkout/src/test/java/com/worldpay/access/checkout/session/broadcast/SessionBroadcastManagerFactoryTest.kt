package com.worldpay.access.checkout.session.broadcast

import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import kotlin.test.assertNotNull
import org.junit.Test
import org.mockito.Mockito.mock

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
