package com.worldpay.access.checkout.session.request.handlers

import android.content.Context
import android.content.Intent
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.session.CVVSessionRequest
import com.worldpay.access.checkout.api.session.SessionRequestService
import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.SessionType
import com.worldpay.access.checkout.client.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
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
        assertTrue { paymentsCvcSessionRequestHandler.canHandle(listOf(PAYMENTS_CVC_SESSION)) }
    }

    @Test
    fun `should not be able to handle a verified token request`() {
        assertFalse { paymentsCvcSessionRequestHandler.canHandle(listOf(VERIFIED_TOKEN_SESSION)) }
    }

    @Test
    fun `should not throw illegal argument exception if pan is not provided in card details`() {
        val cardDetails = CardDetails.Builder()
            .expiryDate(10, 20)
            .cvv("123")
            .build()
        paymentsCvcSessionRequestHandler.handle(cardDetails)
    }

    @Test
    fun `should not throw illegal argument exception if expiry date is not provided in card details`() {
        val cardDetails = CardDetails.Builder()
            .pan("123456789")
            .cvv("123")
            .build()
        paymentsCvcSessionRequestHandler.handle(cardDetails)
    }

    @Test
    fun `should throw illegal argument exception if cvv is not provided in card details`() {
        val cardDetails = CardDetails.Builder().build()

        val exception = assertFailsWith<IllegalArgumentException> {
            paymentsCvcSessionRequestHandler.handle(cardDetails)
        }

        assertEquals("Expected cvv to be provided but was not", exception.message)
    }

    @Test
    fun `should notify external session response listener when request has started`() {
        val cardDetails = CardDetails.Builder()
            .cvv("123")
            .build()

        paymentsCvcSessionRequestHandler.handle(cardDetails)

        verify(externalSessionResponseListener).onRequestStarted()
    }

    @Test
    fun `should start service via context using the expected intent`() {
        val cardDetails = CardDetails.Builder()
            .cvv("123")
            .build()

        paymentsCvcSessionRequestHandler.handle(cardDetails)

        val argument = ArgumentCaptor.forClass(Intent::class.java)

        verify(context).startService(argument.capture())

        val cvvSessionRequest = argument.value.getSerializableExtra(SessionRequestService.REQUEST_KEY) as CVVSessionRequest
        val baseUrl = argument.value.getStringExtra(SessionRequestService.BASE_URL_KEY)
        val discoverLinks = argument.value.getSerializableExtra(SessionRequestService.DISCOVER_LINKS) as DiscoverLinks
        val sessionType = argument.value.getSerializableExtra(SessionRequestService.SESSION_TYPE) as SessionType

        assertEquals("merchant-id", cvvSessionRequest.identity)
        assertEquals(cardDetails.cvv, cvvSessionRequest.cvv)

        assertEquals("base-url", baseUrl)

        assertEquals(DiscoverLinks.sessions, discoverLinks)
        assertEquals(PAYMENTS_CVC_SESSION, sessionType)

        assertEquals(4, argument.value.extras?.size())
    }

}