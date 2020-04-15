package com.worldpay.access.checkout.client.checkout

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.card.CardDetails
import com.worldpay.access.checkout.client.token.TokenRequest.SESSION_TOKEN
import com.worldpay.access.checkout.client.token.TokenRequest.VERIFIED_TOKEN
import com.worldpay.access.checkout.token.TokenRequestHandlerFactory
import com.worldpay.access.checkout.token.handlers.SessionTokenRequestRequestHandler
import com.worldpay.access.checkout.token.handlers.VerifiedTokenRequestRequestHandler
import com.worldpay.access.checkout.views.SessionResponseListener
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

class AccessCheckoutClientTest {

    private val context = mock(Context::class.java)
    private val sessionResponseListener = mock(SessionResponseListener::class.java)
    private val lifecycleOwner = mock(LifecycleOwner::class.java)
    private val lifecycle = mock(Lifecycle::class.java)

    private val sessionTokenRequestHandlerMock = mock(SessionTokenRequestRequestHandler::class.java)
    private val verifiedTokenRequestHandlerMock = mock(VerifiedTokenRequestRequestHandler::class.java)
    private val tokenHandlerFactoryMock = mock(TokenRequestHandlerFactory::class.java)
    private val merchantId = "merchant-123"
    private val baseUrl = "http://localhost"

    private lateinit var accessCheckoutClient: CheckoutClient

    @Before
    fun setup() {
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)
        accessCheckoutClient = AccessCheckoutClient(
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
        val accessCheckoutClient = AccessCheckoutClient(
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

        val tokenRequests = listOf(VERIFIED_TOKEN, SESSION_TOKEN)

        accessCheckoutClient.generateSession(cardDetails, tokenRequests)

        verify(sessionTokenRequestHandlerMock).canHandle(tokenRequests)
        verify(verifiedTokenRequestHandlerMock).canHandle(tokenRequests)
    }

    @Test
    fun `should call handle method on the sessionTokenRequestHandler when calling generate for session token`() {
        val cardDetails = CardDetails.Builder()
            .cvv("123")
            .build()

        val tokenRequests = listOf(SESSION_TOKEN)
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

        val tokenRequests = listOf(VERIFIED_TOKEN)
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