package com.worldpay.access.checkout.token.handlers

import android.content.Context
import android.content.Intent
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.session.CVVSessionRequest
import com.worldpay.access.checkout.api.session.SessionRequestService
import com.worldpay.access.checkout.client.card.CardDetails
import com.worldpay.access.checkout.client.token.TokenRequest.SESSION_TOKEN
import com.worldpay.access.checkout.client.token.TokenRequest.VERIFIED_TOKEN
import com.worldpay.access.checkout.token.TokenRequestHandlerConfig
import com.worldpay.access.checkout.views.SessionResponseListener
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class SessionTokenRequestRequestHandlerTest {

    private val context = Mockito.mock(Context::class.java)
    private val externalSessionResponseListener = Mockito.mock(SessionResponseListener::class.java)

    private lateinit var sessionTokenRequestRequestHandler: SessionTokenRequestRequestHandler

    @Before
    fun setup() {
        sessionTokenRequestRequestHandler =
            SessionTokenRequestRequestHandler(
                TokenRequestHandlerConfig.Builder()
                    .baseUrl("base-url")
                    .merchantId("merchant-id")
                    .context(context)
                    .externalSessionResponseListener(externalSessionResponseListener)
                    .build()
            )
    }

    @Test
    fun `should be able to handle a session token request`() {
        assertTrue { sessionTokenRequestRequestHandler.canHandle(listOf(SESSION_TOKEN)) }
    }

    @Test
    fun `should not be able to handle a verified token request`() {
        assertFalse { sessionTokenRequestRequestHandler.canHandle(listOf(VERIFIED_TOKEN)) }
    }

    @Test
    fun `should not throw illegal argument exception if pan is not provided in card details`() {
        val cardDetails = CardDetails.Builder()
            .expiryDate(10, 20)
            .cvv("123")
            .build()
        sessionTokenRequestRequestHandler.handle(cardDetails)
    }

    @Test
    fun `should not throw illegal argument exception if expiry date is not provided in card details`() {
        val cardDetails = CardDetails.Builder()
            .pan("123456789")
            .cvv("123")
            .build()
        sessionTokenRequestRequestHandler.handle(cardDetails)
    }

    @Test
    fun `should throw illegal argument exception if cvv is not provided in card details`() {
        val cardDetails = CardDetails.Builder().build()

        val exception = assertFailsWith<IllegalArgumentException> {
            sessionTokenRequestRequestHandler.handle(cardDetails)
        }

        assertEquals("Expected cvv to be provided but was not", exception.message)
    }

    @Test
    fun `should notify external session response listener when request has started`() {
        val cardDetails = CardDetails.Builder()
            .cvv("123")
            .build()

        sessionTokenRequestRequestHandler.handle(cardDetails)

        verify(externalSessionResponseListener).onRequestStarted()
    }

    @Test
    fun `should start service via context using the expected intent`() {
        val cardDetails = CardDetails.Builder()
            .cvv("123")
            .build()

        sessionTokenRequestRequestHandler.handle(cardDetails)

        val argument = ArgumentCaptor.forClass(Intent::class.java)

        verify(context).startService(argument.capture())

        val cvvSessionRequest = argument.value.getSerializableExtra(SessionRequestService.REQUEST_KEY) as CVVSessionRequest
        val baseUrl = argument.value.getStringExtra(SessionRequestService.BASE_URL_KEY)
        val discoverLinks =
            argument.value.getSerializableExtra(SessionRequestService.DISCOVER_LINKS) as DiscoverLinks

        assertEquals("merchant-id", cvvSessionRequest.identity)
        assertEquals(cardDetails.cvv, cvvSessionRequest.cvv)

        assertEquals("base-url", baseUrl)

        assertEquals(DiscoverLinks.sessions, discoverLinks)
    }

}