package com.worldpay.access.checkout.api.session

import com.worldpay.access.checkout.api.Callback
import org.junit.Assert.assertNotNull
import org.junit.Test

class RequestDispatcherFactoryTest {

    @Test
    fun shouldCreateRequestDispatcherInstance() {
        val sessionResponseCallback = object :
            Callback<SessionResponse> {
            override fun onResponse(error: Exception?, response: SessionResponse?) { }
        }

        val instance = RequestDispatcherFactory()
            .getInstance("some path", sessionResponseCallback)

        assertNotNull(instance)
    }
}