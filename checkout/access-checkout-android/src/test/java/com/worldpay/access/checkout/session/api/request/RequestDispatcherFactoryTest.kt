package com.worldpay.access.checkout.session.api.request

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.session.api.SessionResponseInfo
import com.worldpay.access.checkout.session.api.client.SessionClient
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.Mockito.mock

class RequestDispatcherFactoryTest {

    @Test
    fun `should be able to create a new request dispatcher instance`() {
        val sessionResponseCallback = object :
            Callback<SessionResponseInfo> {
            override fun onResponse(error: Exception?, response: SessionResponseInfo?) {}
        }

        val requestDispatcherFactory = RequestDispatcherFactory()
        val sessionClient = mock(SessionClient::class.java)

        val instance = requestDispatcherFactory.getInstance("some path", sessionClient, sessionResponseCallback)

        assertNotNull(instance)
    }

}