package com.worldpay.access.checkout.token.handlers

import android.content.Context
import android.content.Intent
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.session.CardSessionRequest
import com.worldpay.access.checkout.api.session.SessionRequestService
import com.worldpay.access.checkout.api.session.SessionRequestService.Companion.REQUEST_KEY
import com.worldpay.access.checkout.client.card.CardDetailsBuilder
import com.worldpay.access.checkout.client.token.TokenRequest.SESSION_TOKEN
import com.worldpay.access.checkout.client.token.TokenRequest.VERIFIED_TOKEN
import com.worldpay.access.checkout.token.TokenRequestHandlerConfig
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
class VerifiedTokenRequestRequestHandlerTest {

    private val context = mock(Context::class.java)
    private val externalSessionResponseListener = mock(SessionResponseListener::class.java)

    private lateinit var verifiedTokenRequestRequestHandler: VerifiedTokenRequestRequestHandler

    @Before
    fun setup() {
        verifiedTokenRequestRequestHandler =
            VerifiedTokenRequestRequestHandler(
                TokenRequestHandlerConfig.Builder()
                    .baseUrl("base-url")
                    .merchantId("merchant-id")
                    .context(context)
                    .externalSessionResponseListener(externalSessionResponseListener)
                    .build()
            )
    }

    @Test
    fun `should be able to handle a verified token request`() {
        assertTrue { verifiedTokenRequestRequestHandler.canHandle(listOf(VERIFIED_TOKEN)) }
    }

    @Test
    fun `should not be able to handle a session token request`() {
        assertFalse { verifiedTokenRequestRequestHandler.canHandle(listOf(SESSION_TOKEN)) }
    }

    @Test
    fun `should throw illegal argument exception if pan is not provided in card details`() {
        val cardDetails = CardDetailsBuilder()
            .expiryDate(12, 2020)
            .cvv("123")
            .build()

        val exception = assertFailsWith<IllegalArgumentException> {
            verifiedTokenRequestRequestHandler.handle(cardDetails)
        }

        assertEquals("Expected pan to be provided but was not", exception.message)
    }

    @Test
    fun `should throw illegal argument exception if expiry date is not provided in card details`() {
        val cardDetails = CardDetailsBuilder()
            .pan("1234")
            .cvv("123")
            .build()

        val exception = assertFailsWith<IllegalArgumentException> {
            verifiedTokenRequestRequestHandler.handle(cardDetails)
        }

        assertEquals("Expected expiry date to be provided but was not", exception.message)
    }

    @Test
    fun `should throw illegal argument exception if cvv is not provided in card details`() {
        val cardDetails = CardDetailsBuilder()
            .pan("1234")
            .expiryDate(12, 2020)
            .build()

        val exception = assertFailsWith<IllegalArgumentException> {
            verifiedTokenRequestRequestHandler.handle(cardDetails)
        }

        assertEquals("Expected cvv to be provided but was not", exception.message)
    }

    @Test
    fun `should notify external session response listener when request has started`() {
        val cardDetails = CardDetailsBuilder()
            .pan("1234")
            .expiryDate(12, 2020)
            .cvv("123")
            .build()

        verifiedTokenRequestRequestHandler.handle(cardDetails)

        verify(externalSessionResponseListener).onRequestStarted()
    }

    @Test
    fun `should start service via context using the expected intent`() {
        val cardDetails = CardDetailsBuilder()
            .pan("1234")
            .expiryDate(12, 2020)
            .cvv("123")
            .build()

        verifiedTokenRequestRequestHandler.handle(cardDetails)

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