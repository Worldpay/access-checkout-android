package com.worldpay.access.checkout.session

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.AccessCheckoutClient
import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
import com.worldpay.access.checkout.session.request.SessionRequestHandlerFactory
import com.worldpay.access.checkout.session.request.handlers.PaymentsCvcSessionRequestHandler
import com.worldpay.access.checkout.session.request.handlers.VerifiedTokensSessionRequestHandler
import com.worldpay.access.checkout.views.SessionResponseListener
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

class AccessCheckoutClientImplTest {

    private val context = mock(Context::class.java)
    private val sessionResponseListener = mock(SessionResponseListener::class.java)
    private val lifecycleOwner = mock(LifecycleOwner::class.java)
    private val lifecycle = mock(Lifecycle::class.java)

    private val sessionTokenRequestHandlerMock = mock(PaymentsCvcSessionRequestHandler::class.java)
    private val verifiedTokenRequestHandlerMock = mock(VerifiedTokensSessionRequestHandler::class.java)
    private val tokenHandlerFactoryMock = mock(SessionRequestHandlerFactory::class.java)
    private val baseUrl = "http://localhost"

    private lateinit var accessCheckoutClient: AccessCheckoutClient

    @Before
    fun setup() {
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)
        accessCheckoutClient =
            AccessCheckoutClientImpl(
                baseUrl,
                context,
                sessionResponseListener,
                lifecycleOwner,
                tokenHandlerFactoryMock
            )

        val handlers = listOf(sessionTokenRequestHandlerMock, verifiedTokenRequestHandlerMock)
        given(tokenHandlerFactoryMock.getTokenHandlers()).willReturn(handlers)
    }

    @Test
    fun `given AccessCheckoutClient is initialised with mandatory arguments then an instance of SDK is returned`() {
        val accessCheckoutClient =
            AccessCheckoutClientImpl(
                baseUrl,
                context,
                sessionResponseListener,
                lifecycleOwner,
                tokenHandlerFactoryMock
            )

        assertNotNull(accessCheckoutClient)
    }

    @Test
    fun `should be able to call each handler's canHandle method when calling generate`() {
        val cardDetails = CardDetails.Builder()
            .pan("1234")
            .expiryDate(12, 2020)
            .cvv("123")
            .build()

        val tokenRequests = listOf(VERIFIED_TOKEN_SESSION, PAYMENTS_CVC_SESSION)

        accessCheckoutClient.generateSession(cardDetails, tokenRequests)

        verify(sessionTokenRequestHandlerMock).canHandle(tokenRequests)
        verify(verifiedTokenRequestHandlerMock).canHandle(tokenRequests)
    }

    @Test
    fun `should call handle method on the sessionTokenRequestHandler when calling generate for session token`() {
        val cardDetails = CardDetails.Builder()
            .cvv("123")
            .build()

        val tokenRequests = listOf(PAYMENTS_CVC_SESSION)
        given(sessionTokenRequestHandlerMock.canHandle(tokenRequests)).willCallRealMethod()

        accessCheckoutClient.generateSession(cardDetails, tokenRequests)

        verify(sessionTokenRequestHandlerMock).canHandle(tokenRequests)
        verify(sessionTokenRequestHandlerMock).handle(cardDetails)
        verify(verifiedTokenRequestHandlerMock, never()).handle(cardDetails)
    }

    @Test
    fun `should call handle method on the verifiedTokenRequestHandler when calling generate for verified token`() {
        val cardDetails = CardDetails.Builder()
            .pan("1234")
            .expiryDate(12, 2020)
            .cvv("123")
            .build()

        val tokenRequests = listOf(VERIFIED_TOKEN_SESSION)
        given(verifiedTokenRequestHandlerMock.canHandle(tokenRequests)).willCallRealMethod()

        accessCheckoutClient.generateSession(cardDetails, tokenRequests)

        verify(verifiedTokenRequestHandlerMock).canHandle(tokenRequests)
        verify(verifiedTokenRequestHandlerMock).handle(cardDetails)
        verify(sessionTokenRequestHandlerMock, never()).handle(cardDetails)
    }

    @Test
    fun `should not call handle when canHandle returns false`() {
        val cardDetails = CardDetails.Builder()
            .pan("1234")
            .expiryDate(12, 2020)
            .cvv("123")
            .build()

        accessCheckoutClient.generateSession(cardDetails, emptyList())

        verify(verifiedTokenRequestHandlerMock, never()).handle(cardDetails)
        verify(sessionTokenRequestHandlerMock, never()).handle(cardDetails)
    }

}