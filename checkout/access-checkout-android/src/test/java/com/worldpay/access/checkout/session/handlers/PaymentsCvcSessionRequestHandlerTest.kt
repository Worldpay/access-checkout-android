package com.worldpay.access.checkout.session.handlers

import android.content.Context
import android.content.Intent
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.client.session.model.CardDetails
import com.worldpay.access.checkout.client.session.model.SessionType.PAYMENTS_CVC
import com.worldpay.access.checkout.client.session.model.SessionType.VERIFIED_TOKENS
import com.worldpay.access.checkout.session.api.SessionRequestService.Companion.REQUEST_KEY
import com.worldpay.access.checkout.session.api.request.CvcSessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
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
class PaymentsCvcSessionRequestHandlerTest {

    private val context = Mockito.mock(Context::class.java)
    private val externalSessionResponseListener = Mockito.mock(SessionResponseListener::class.java)

    private lateinit var paymentsCvcSessionRequestHandler: PaymentsCvcSessionRequestHandler

    @Before
    fun setup() {
        paymentsCvcSessionRequestHandler =
            PaymentsCvcSessionRequestHandler(
                SessionRequestHandlerConfig.Builder()
                    .baseUrl("base-url")
                    .merchantId("merchant-id")
                    .context(context)
                    .externalSessionResponseListener(externalSessionResponseListener)
                    .build()
            )
    }

    @Test
    fun `should be able to handle a session token request`() {
        assertTrue { paymentsCvcSessionRequestHandler.canHandle(listOf(PAYMENTS_CVC)) }
    }

    @Test
    fun `should not be able to handle a verified token request`() {
        assertFalse { paymentsCvcSessionRequestHandler.canHandle(listOf(VERIFIED_TOKENS)) }
    }

    @Test
    fun `should not throw illegal argument exception if pan is not provided in card details`() {
        val cardDetails = CardDetails.Builder()
            .expiryDate("1020")
            .cvc("123")
            .build()
        paymentsCvcSessionRequestHandler.handle(cardDetails)
    }

    @Test
    fun `should not throw illegal argument exception if expiry date is not provided in card details`() {
        val cardDetails = CardDetails.Builder()
            .pan("123456789")
            .cvc("123")
            .build()
        paymentsCvcSessionRequestHandler.handle(cardDetails)
    }

    @Test
    fun `should throw illegal argument exception if cvc is not provided in card details`() {
        val cardDetails = CardDetails.Builder().build()

        val exception = assertFailsWith<IllegalArgumentException> {
            paymentsCvcSessionRequestHandler.handle(cardDetails)
        }

        assertEquals("Expected cvc to be provided but was not", exception.message)
    }

    @Test
    fun `should start service via context using the expected intent`() {
        val cardDetails = CardDetails.Builder()
            .cvc("123")
            .build()

        paymentsCvcSessionRequestHandler.handle(cardDetails)

        val argument = ArgumentCaptor.forClass(Intent::class.java)

        verify(context).startService(argument.capture())

        val sessionRequestInfo = argument.value.getSerializableExtra(REQUEST_KEY) as SessionRequestInfo

        sessionRequestInfo.requestBody as CvcSessionRequest

        assertEquals("merchant-id", sessionRequestInfo.requestBody.identity)
        assertEquals(cardDetails.cvc, sessionRequestInfo.requestBody.cvc)

        assertEquals("base-url", sessionRequestInfo.baseUrl)

        assertEquals(DiscoverLinks.sessions, sessionRequestInfo.discoverLinks)
        assertEquals(PAYMENTS_CVC, sessionRequestInfo.sessionType)

        assertEquals(1, argument.value.extras?.size())
    }

}
