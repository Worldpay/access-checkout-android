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
import com.worldpay.access.checkout.testutils.PlainRobolectricTestRunner
import com.worldpay.access.checkout.testutils.createAccessEditTextMock
import java.net.URL
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

@RunWith(PlainRobolectricTestRunner::class)
class CardSessionRequestHandlerTest {

    private val context = mock(Context::class.java)
    private val externalSessionResponseListener = mock(SessionResponseListener::class.java)

    private lateinit var cardSessionRequestHandler: CardSessionRequestHandler

    private val baseUrl = URL("http://base-url.com")

    @Before
    fun setup() {
        cardSessionRequestHandler =
            CardSessionRequestHandler(
                SessionRequestHandlerConfig.Builder()
                    .baseUrl(baseUrl)
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
        val expiryDate = createAccessEditTextMock("1120")
        val cvc = createAccessEditTextMock("123")

        val cardDetails = CardDetails.Builder()
            .expiryDate(expiryDate)
            .cvc(cvc)
            .build()

        val exception = assertFailsWith<IllegalArgumentException> {
            cardSessionRequestHandler.handle(cardDetails)
        }

        assertEquals("Expected pan to be provided but was not", exception.message)
    }

    @Test
    fun `should throw illegal argument exception if expiry date is not provided in card details`() {
        val pan = createAccessEditTextMock("1234")
        val cvc = createAccessEditTextMock("123")

        val cardDetails = CardDetails.Builder()
            .pan(pan)
            .cvc(cvc)
            .build()

        val exception = assertFailsWith<IllegalArgumentException> {
            cardSessionRequestHandler.handle(cardDetails)
        }

        assertEquals("Expected expiry date to be provided but was not", exception.message)
    }

    @Test
    fun `should throw illegal argument exception if cvc is not provided in card details`() {
        val pan = createAccessEditTextMock("1234")
        val expiryDate = createAccessEditTextMock("1120")

        val cardDetails = CardDetails.Builder()
            .pan(pan)
            .expiryDate(expiryDate)
            .build()

        val exception = assertFailsWith<IllegalArgumentException> {
            cardSessionRequestHandler.handle(cardDetails)
        }

        assertEquals("Expected cvc to be provided but was not", exception.message)
    }

    @Test
    fun `should start service via context using the expected intent`() {
        val pan = createAccessEditTextMock("1234")
        val expiryDate = createAccessEditTextMock("1120")
        val cvc = createAccessEditTextMock("123")

        val cardDetails = CardDetails.Builder()
            .pan(pan)
            .expiryDate(expiryDate)
            .cvc(cvc)
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

        assertEquals(baseUrl, sessionRequestInfo.baseUrl)

        assertEquals(DiscoverLinks.cardSessions, sessionRequestInfo.discoverLinks)
        assertEquals(CARD, sessionRequestInfo.sessionType)

        assertEquals(1, argument.value.extras?.size())
    }
}
