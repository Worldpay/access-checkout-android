package com.worldpay.access.checkout.session.request.handlers

import android.content.Context
import android.content.Intent
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.session.CardSessionRequest
import com.worldpay.access.checkout.api.session.SessionRequestService
import com.worldpay.access.checkout.api.session.SessionRequestService.Companion.REQUEST_KEY
import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
import com.worldpay.access.checkout.session.request.SessionRequestHandlerConfig
import com.worldpay.access.checkout.views.SessionResponseListener
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class VerifiedTokensSessionTypeHandlerTest {

    private val context = mock(Context::class.java)
    private val externalSessionResponseListener = mock(SessionResponseListener::class.java)

    private lateinit var verifiedTokensSessionRequestHandler: VerifiedTokensSessionRequestHandler

    @Before
    fun setup() {
        verifiedTokensSessionRequestHandler =
            VerifiedTokensSessionRequestHandler(
                SessionRequestHandlerConfig.Builder()
                    .baseUrl("base-url")
                    .merchantId("merchant-id")
                    .context(context)
                    .externalSessionResponseListener(externalSessionResponseListener)
                    .build()
            )
    }

    @Test
    fun `should be able to handle a verified token request`() {
        assertTrue { verifiedTokensSessionRequestHandler.canHandle(listOf(VERIFIED_TOKEN_SESSION)) }
    }

    @Test
    fun `should not be able to handle a session token request`() {
        assertFalse { verifiedTokensSessionRequestHandler.canHandle(listOf(PAYMENTS_CVC_SESSION)) }
    }

    @Test
    fun `should throw illegal argument exception if pan is not provided in card details`() {
        val cardDetails = CardDetails.Builder()
            .expiryDate(12, 2020)
            .cvv("123")
            .build()

        val exception = assertFailsWith<IllegalArgumentException> {
            verifiedTokensSessionRequestHandler.handle(cardDetails)
        }

        assertEquals("Expected pan to be provided but was not", exception.message)
    }

    @Test
    fun `should throw illegal argument exception if expiry date is not provided in card details`() {
        val cardDetails = CardDetails.Builder()
            .pan("1234")
            .cvv("123")
            .build()

        val exception = assertFailsWith<IllegalArgumentException> {
            verifiedTokensSessionRequestHandler.handle(cardDetails)
        }

        assertEquals("Expected expiry date to be provided but was not", exception.message)
    }

    @Test
    fun `should throw illegal argument exception if cvv is not provided in card details`() {
        val cardDetails = CardDetails.Builder()
            .pan("1234")
            .expiryDate(12, 2020)
            .build()

        val exception = assertFailsWith<IllegalArgumentException> {
            verifiedTokensSessionRequestHandler.handle(cardDetails)
        }

        assertEquals("Expected cvv to be provided but was not", exception.message)
    }

    @Test
    fun `should notify external session response listener when request has started`() {
        val cardDetails = CardDetails.Builder()
            .pan("1234")
            .expiryDate(12, 2020)
            .cvv("123")
            .build()

        verifiedTokensSessionRequestHandler.handle(cardDetails)

        verify(externalSessionResponseListener).onRequestStarted()
    }

    @Test
    fun `should start service via context using the expected intent`() {
        val cardDetails = CardDetails.Builder()
            .pan("1234")
            .expiryDate(12, 2020)
            .cvv("123")
            .build()

        verifiedTokensSessionRequestHandler.handle(cardDetails)

        val argument = ArgumentCaptor.forClass(Intent::class.java)

        verify(context).startService(argument.capture())

        val cardSessionRequest = argument.value.getSerializableExtra(REQUEST_KEY) as CardSessionRequest
        val baseUrl = argument.value.getStringExtra(SessionRequestService.BASE_URL_KEY)
        val discoverLinks =
            argument.value.getSerializableExtra(SessionRequestService.DISCOVER_LINKS) as DiscoverLinks

        assertEquals(cardDetails.pan, cardSessionRequest.cardNumber)
        assertEquals("merchant-id", cardSessionRequest.identity)
        assertEquals(cardDetails.cvv, cardSessionRequest.cvv)
        assertEquals(cardDetails.expiryDate?.month, cardSessionRequest.cardExpiryDate.month)
        assertEquals(cardDetails.expiryDate?.year, cardSessionRequest.cardExpiryDate.year)

        assertEquals("base-url", baseUrl)

        assertEquals(DiscoverLinks.verifiedTokens, discoverLinks)
    }

}