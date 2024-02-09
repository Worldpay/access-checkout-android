package com.worldpay.access.checkout.client.session

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import java.lang.reflect.Field
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock

class AccessCheckoutClientBuilderTest {

    private val context = mock(Context::class.java)
    private val sessionResponseListener = mock(SessionResponseListener::class.java)
    private val lifecycleOwner = mock(LifecycleOwner::class.java)
    private val checkoutId = "checkout-id-123"
    private val baseUrl = "https://localhost:8443"

    @Before
    fun setUp() {
        given(lifecycleOwner.lifecycle).willReturn(mock(Lifecycle::class.java))
    }

    @Test
    fun `should successfully initialise access checkout client using a builder`() {
        given(context.applicationContext).willReturn(context)

        val accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(baseUrl)
            .checkoutId(checkoutId)
            .context(context)
            .sessionResponseListener(sessionResponseListener)
            .lifecycleOwner(lifecycleOwner)
            .build()

        assertNotNull(accessCheckoutClient)
    }

    @Test
    fun `should successfully sanitise baseUrl if it has a trailing forward slash`() {
        given(context.applicationContext).willReturn(context)

        val baseUrlWithTrailingSlash = "$baseUrl/"

        val accessCheckoutClientBuilder = AccessCheckoutClientBuilder()
            .baseUrl(baseUrlWithTrailingSlash)

        val builderPrivateField: Field =
            AccessCheckoutClientBuilder::class.java.getDeclaredField("baseUrl")
        builderPrivateField.isAccessible = true
        val baseUrlValueInBuilder = builderPrivateField[accessCheckoutClientBuilder] as String

        assertEquals(baseUrlValueInBuilder, baseUrl)
    }

    @Test
    fun `should throw an illegal argument exception when no baseUrl is passed to builder`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            AccessCheckoutClientBuilder()
                .checkoutId(checkoutId)
                .context(context)
                .sessionResponseListener(sessionResponseListener)
                .lifecycleOwner(lifecycleOwner)
                .build()
        }
        assertEquals("Expected base url to be provided but was not", exception.message)
    }

    @Test
    fun `should throw an illegal argument exception when no checkout ID is passed to builder`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            AccessCheckoutClientBuilder()
                .baseUrl(baseUrl)
                .context(context)
                .sessionResponseListener(sessionResponseListener)
                .lifecycleOwner(lifecycleOwner)
                .build()
        }
        assertEquals("Expected checkout id to be provided but was not", exception.message)
    }

    @Test
    fun `should throw an illegal argument exception when no context is passed to builder`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            AccessCheckoutClientBuilder()
                .baseUrl(baseUrl)
                .checkoutId(checkoutId)
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
                .checkoutId(checkoutId)
                .context(context)
                .lifecycleOwner(lifecycleOwner)
                .build()
        }
        assertEquals(
            "Expected session response listener to be provided but was not",
            exception.message
        )
    }

    @Test
    fun `should throw an illegal argument exception when no lifecycle owner is passed to builder`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            AccessCheckoutClientBuilder()
                .baseUrl(baseUrl)
                .checkoutId(checkoutId)
                .context(context)
                .sessionResponseListener(sessionResponseListener)
                .build()
        }
        assertEquals("Expected lifecycle owner to be provided but was not", exception.message)
    }
}
