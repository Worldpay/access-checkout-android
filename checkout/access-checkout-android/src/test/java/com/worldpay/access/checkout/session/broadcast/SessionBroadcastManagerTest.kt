package com.worldpay.access.checkout.session.broadcast

import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastReceiver
import com.worldpay.access.checkout.views.SessionResponseListener
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.assertEquals

class SessionBroadcastManagerTest {

    @Test
    fun `should register expected broadcast receivers when calling register`() {
        val localBroadcastManagerFactoryMock = mock(LocalBroadcastManagerFactory::class.java)
        val externalSessionResponseListenerMock = mock(SessionResponseListener::class.java)
        val localBroadcastManagerMock = mock(LocalBroadcastManager::class.java)

        val argument = ArgumentCaptor.forClass(IntentFilter::class.java)

        given(localBroadcastManagerFactoryMock.createInstance()).willReturn(localBroadcastManagerMock)

        val sessionBroadcastManager = SessionBroadcastManager(
            localBroadcastManagerFactoryMock,
            externalSessionResponseListenerMock
        )

        sessionBroadcastManager.register()

        verify(localBroadcastManagerMock).registerReceiver(any(SessionBroadcastReceiver::class.java), argument.capture())

        assertEquals(SessionBroadcastReceiver().getIntentFilter().countActions(), argument.value.countActions())
        assertEquals(SessionBroadcastReceiver().getIntentFilter().getAction(0), argument.value.getAction(0))
    }

    @Test
    fun `should unregister expected broadcast receivers when calling unregister`() {
        val localBroadcastManagerFactoryMock = mock(LocalBroadcastManagerFactory::class.java)
        val externalSessionResponseListenerMock = mock(SessionResponseListener::class.java)
        val localBroadcastManagerMock = mock(LocalBroadcastManager::class.java)

        given(localBroadcastManagerFactoryMock.createInstance()).willReturn(localBroadcastManagerMock)

        val sessionBroadcastManager = SessionBroadcastManager(
            localBroadcastManagerFactoryMock,
            externalSessionResponseListenerMock
        )

        sessionBroadcastManager.unregister()

        verify(localBroadcastManagerMock).unregisterReceiver(any(SessionBroadcastReceiver::class.java))
    }

}