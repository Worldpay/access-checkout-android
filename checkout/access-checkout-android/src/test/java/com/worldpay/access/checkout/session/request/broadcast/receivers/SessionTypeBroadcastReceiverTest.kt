package com.worldpay.access.checkout.session.request.broadcast.receivers

import android.content.Context
import android.content.Intent
import com.worldpay.access.checkout.session.request.broadcast.receivers.SessionTypeBroadcastReceiver.Companion.NUMBER_OF_SESSION_TYPES
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class SessionTypeBroadcastReceiverTest {

    @Test
    fun `should have expected constants`() {
        assertEquals("number-of-session-types", NUMBER_OF_SESSION_TYPES)
    }

    @Test
    fun `should do nothing when calling onReceive`() {
        val contextMock = mock(Context::class.java)
        val intentMock = mock(Intent::class.java)
        val sessionTypeBroadcastReceiver = SessionTypeBroadcastReceiver()

        sessionTypeBroadcastReceiver.onReceive(contextMock, intentMock)
    }

    @Test
    fun `should return expected IntentFilter`() {
        val sessionTypeBroadcastReceiver = SessionTypeBroadcastReceiver()
        val intentFilter = sessionTypeBroadcastReceiver.getIntentFilter()

        assertEquals(NUM_OF_SESSION_TYPES_REQUESTED, intentFilter.getAction(0))
        assertEquals(SESSION_TYPE_REQUEST_COMPLETE, intentFilter.getAction(1))

        assertEquals(2, intentFilter.countActions())
    }

}