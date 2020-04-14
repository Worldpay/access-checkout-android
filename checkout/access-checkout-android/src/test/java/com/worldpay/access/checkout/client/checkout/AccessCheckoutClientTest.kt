package com.worldpay.access.checkout.client.checkout

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.any
import com.worldpay.access.checkout.views.SessionResponseListener
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.*

class AccessCheckoutClientTest {

    private val context = mock(Context::class.java)
    private val sessionResponseListener = mock(SessionResponseListener::class.java)
    private val lifecycleOwner = mock(LifecycleOwner::class.java)
    private val lifecycle = mock(Lifecycle::class.java)
    private val merchantId = "merchant-123"
    private val baseUrl = "http://localhost"

    private lateinit var accessCheckoutClient: AccessCheckoutClient

    @Before
    fun setup() {
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)
        accessCheckoutClient = AccessCheckoutClient.init(baseUrl, merchantId, sessionResponseListener, context, lifecycleOwner)
    }

    @Test
    fun `given AccessCheckoutClient is initialised with mandatory arguments then an instance of SDK is returned`() {
        val accessCheckoutClient = AccessCheckoutClient.init(baseUrl, merchantId, sessionResponseListener, context, lifecycleOwner)

        assertNotNull(accessCheckoutClient)
    }

    @Test
    fun `given the user requests a session reference then the session request service is started`() {
        val cardNumber = "1234"
        val month = 12
        val year = 2020
        val cvv = "123"

        accessCheckoutClient.generateSessionState(cardNumber, month, year, cvv)

        then(sessionResponseListener)
            .should()
            .onRequestStarted()

        then(context)
            .should()
            .startService(any())
    }

}