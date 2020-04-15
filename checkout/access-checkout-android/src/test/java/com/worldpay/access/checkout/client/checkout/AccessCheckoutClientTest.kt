package com.worldpay.access.checkout.client.checkout

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.any
import com.worldpay.access.checkout.client.card.CardDetailsBuilder
import com.worldpay.access.checkout.views.SessionResponseListener
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AccessCheckoutClientTest {

    private val context = mock(Context::class.java)
    private val sessionResponseListener = mock(SessionResponseListener::class.java)
    private val lifecycleOwner = mock(LifecycleOwner::class.java)
    private val lifecycle = mock(Lifecycle::class.java)
    private val merchantId = "merchant-123"
    private val baseUrl = "http://localhost"

    private lateinit var accessCheckoutClient: CheckoutClient

    @Before
    fun setup() {
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)
        accessCheckoutClient = AccessCheckoutClient(
            baseUrl,
            merchantId,
            context,
            sessionResponseListener,
            lifecycleOwner
        )
    }

    @Test
    fun `given AccessCheckoutClient is initialised with mandatory arguments then an instance of SDK is returned`() {
        val accessCheckoutClient = AccessCheckoutClient(
            baseUrl,
            merchantId,
            context,
            sessionResponseListener,
            lifecycleOwner
        )

        assertNotNull(accessCheckoutClient)
    }

    @Test
    fun `given the user requests a session reference then the session request service is started`() {
        val cardDetails = CardDetailsBuilder()
            .pan("1234")
            .expiryDate(12, 2020)
            .cvv("123")
            .build()

        accessCheckoutClient.generateSessionState(cardDetails)

        then(sessionResponseListener)
            .should()
            .onRequestStarted()

        then(context)
            .should()
            .startService(any())
    }

    @Test
    fun `should throw an exception when pan is not provided`() {
        val cardDetails = CardDetailsBuilder()
            .cvv("123")
            .expiryDate(10, 20)
            .build()

        val exception = assertFailsWith<IllegalArgumentException> {
            accessCheckoutClient.generateSessionState(cardDetails)
        }

        assertEquals("Expected pan but none provided", exception.message)
    }

    @Test
    fun `should throw an exception when expiry date is not provided`() {
        val cardDetails = CardDetailsBuilder()
            .pan("120392895018742508243")
            .cvv("123")
            .build()

        val exception = assertFailsWith<IllegalArgumentException> {
            accessCheckoutClient.generateSessionState(cardDetails)
        }

        assertEquals("Expected expiry date but none provided", exception.message)
    }

    @Test
    fun `should throw an exception when cvv is not provided`() {
        val cardDetails = CardDetailsBuilder()
            .pan("120392895018742508243")
            .expiryDate(10, 20)
            .build()

        val exception = assertFailsWith<IllegalArgumentException> {
            accessCheckoutClient.generateSessionState(cardDetails)
        }

        assertEquals("Expected cvv but none provided", exception.message)
    }

}