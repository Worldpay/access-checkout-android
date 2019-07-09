package com.worldpay.access.checkout.api

import android.content.Context
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.BDDMockito.given

class LocalBroadcastManagerFactoryTest {

    @Test
    fun `should instantiate local broadcast manager`() {
        val context = mock<Context>()
        val applicationContext = mock<Context>()
        given(context.applicationContext).willReturn(applicationContext)
        given(applicationContext.mainLooper).willReturn(mock())

        val localBroadcastManager = LocalBroadcastManagerFactory(context).createInstance()

        assertNotNull(localBroadcastManager)
    }
}