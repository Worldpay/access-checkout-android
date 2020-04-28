package com.worldpay.access.checkout.session.request.broadcast

import android.content.Context
import android.os.Looper
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

class LocalBroadcastManagerFactoryTest {

    @Test
    fun `should instantiate local broadcast manager`() {
        val contextMock = mock(Context::class.java)
        val applicationContextMock = mock(Context::class.java)

        given(contextMock.applicationContext).willReturn(applicationContextMock)
        given(applicationContextMock.mainLooper).willReturn(mock(Looper::class.java))

        val localBroadcastManagerFactory = LocalBroadcastManagerFactory(contextMock)
        assertNotNull(localBroadcastManagerFactory.createInstance())
    }

}