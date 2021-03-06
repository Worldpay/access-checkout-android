package com.worldpay.access.checkout.session.handlers

import android.content.Context
import android.content.Intent
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.client.session.model.CardDetails
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.client.session.model.SessionType.CVC
import com.worldpay.access.checkout.session.api.SessionRequestService.Companion.REQUEST_KEY
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CardSessionRequestHandlerTest {

    private val context = mock(Context::class.java)
    private val externalSessionResponseListener = mock(SessionResponseListener::class.java)

    private lateinit var cardSessionRequestHandler: CardSessionRequestHandler

    @Before
    fun setup() {
        cardSessionRequestHandler =
            CardSessionRequestHandler(
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
        assertTrue { cardSessionRequestHandler.canHandle(listOf(CARD)) }
    }

    @Test
    fun `should not be able to handle a session token request`() {
        assertFalse { cardSessionRequestHandler.canHandle(listOf(CVC)) }
    }

    @Test
    fun `should throw illegal argument exception if pan is not provided in card details`() {
        val cardDetails = CardDetails.Builder()
            .expiryDate("1220")
            .cvc("123")
            .build()

        val exception = assertFailsWith<IllegalArgumentException> {
            cardSessionRequestHandler.handle(cardDetails)
        }

        assertEquals("Expected pan to be provided but was not", exception.message)
    }

    @Test
    fun `should throw illegal argument exception if expiry date is not provided in card details`() {
        val cardDetails = CardDetails.Builder()
            .pan("1234")
            .cvc("123")
            .build()

        val exception = assertFailsWith<IllegalArgumentException> {
            cardSessionRequestHandler.handle(cardDetails)
        }

        assertEquals("Expected expiry date to be provided but was not", exception.message)
    }

    @Test
    fun `should throw illegal argument exception if cvc is not provided in card details`() {
        val cardDetails = CardDetails.Builder()
            .pan("1234")
            .expiryDate("1220")
            .build()

        val exception = assertFailsWith<IllegalArgumentException> {
            cardSessionRequestHandler.handle(cardDetails)
        }

        assertEquals("Expected cvc to be provided but was not", exception.message)
    }

    @Test
    fun `should start service via context using the expected intent`() {
        val cardDetails = CardDetails.Builder()
            .pan("1234")
            .expiryDate("1220")
            .cvc("123")
            .build()

        cardSessionRequestHandler.handle(cardDetails)

        val argument = ArgumentCaptor.forClass(Intent::class.java)

        verify(context).startService(argument.capture())

        val sessionRequestInfo = argument.value.getSerializableExtra(REQUEST_KEY) as SessionRequestInfo
        sessionRequestInfo.requestBody as CardSessionRequest

        assertEquals(cardDetails.pan, sessionRequestInfo.requestBody.cardNumber)
        assertEquals("merchant-id", sessionRequestInfo.requestBody.identity)
        assertEquals(cardDetails.cvc, sessionRequestInfo.requestBody.cvc)
        assertEquals(cardDetails.expiryDate?.month, sessionRequestInfo.requestBody.cardExpiryDate.month)
        assertEquals(cardDetails.expiryDate?.year, sessionRequestInfo.requestBody.cardExpiryDate.year)

        assertEquals("base-url", sessionRequestInfo.baseUrl)

        assertEquals(DiscoverLinks.verifiedTokens, sessionRequestInfo.discoverLinks)
        assertEquals(CARD, sessionRequestInfo.sessionType)

        assertEquals(1, argument.value.extras?.size())
    }
}
