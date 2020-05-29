package com.worldpay.access.checkout.client

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AccessCheckoutClientBuilderTest {

    private val context = mock(Context::class.java)
    private val sessionResponseListener = mock(SessionResponseListener::class.java)
    private val lifecycleOwner = mock(LifecycleOwner::class.java)
    private val merchantId = "merchant-123"
    private val baseUrl = "http://localhost"

    @Before
    fun setUp() {
        given(lifecycleOwner.lifecycle).willReturn(mock(Lifecycle::class.java))
    }

    @Test
    fun `should successfully initialise access checkout client using a builder`() {
        given(context.applicationContext).willReturn(context)

        val accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(baseUrl)
            .merchantId(merchantId)
            .context(context)
            .sessionResponseListener(sessionResponseListener)
            .lifecycleOwner(lifecycleOwner)
            .build()

        assertNotNull(accessCheckoutClient)
    }

    @Test
    fun `should throw an illegal argument exception when no baseUrl is passed to builder`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            AccessCheckoutClientBuilder()
            .merchantId(merchantId)
            .context(context)
            .sessionResponseListener(sessionResponseListener)
            .lifecycleOwner(lifecycleOwner)
            .build()
        }
        assertEquals("Expected base url to be provided but was not", exception.message)
    }

    @Test
    fun `should throw an illegal argument exception when no merchantId is passed to builder`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            AccessCheckoutClientBuilder()
                .baseUrl(baseUrl)
                .context(context)
                .sessionResponseListener(sessionResponseListener)
                .lifecycleOwner(lifecycleOwner)
                .build()
        }
        assertEquals("Expected merchant ID to be provided but was not", exception.message)
    }

    @Test
    fun `should throw an illegal argument exception when no context is passed to builder`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            AccessCheckoutClientBuilder()
                .baseUrl(baseUrl)
                .merchantId(merchantId)
                .sessionResponseListener(sessionResponseListener)
                .lifecycleOwner(lifecycleOwner)
                .build()
        }
        assertEquals("Expected context to be provided but was not", exception.message)
    }

    @Test
    fun `should throw an illegal argument exception when no Session Response Listener is passed to builder`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            AccessCheckoutClientBuilder()
                .baseUrl(baseUrl)
                .merchantId(merchantId)
                .context(context)
                .lifecycleOwner(lifecycleOwner)
                .build()
        }
        assertEquals("Expected session response listener to be provided but was not", exception.message)
    }

    @Test
    fun `should throw an illegal argument exception when no lifecycle owner is passed to builder`() {
        val exception = assertFailsWith<IllegalArgumentException> {
        AccessCheckoutClientBuilder()
            .baseUrl(baseUrl)
            .merchantId(merchantId)
            .context(context)
            .sessionResponseListener(sessionResponseListener)
            .build()
        }
        assertEquals("Expected lifecycle owner to be provided but was not", exception.message)
    }

}