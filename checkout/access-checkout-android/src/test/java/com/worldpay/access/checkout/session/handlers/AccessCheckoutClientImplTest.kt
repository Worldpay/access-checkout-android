package com.worldpay.access.checkout.session.handlers

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
import com.worldpay.access.checkout.session.AccessCheckoutClientImpl
import com.worldpay.access.checkout.session.ActivityLifecycleObserverInitialiser
import com.worldpay.access.checkout.session.broadcast.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.session.broadcast.receivers.NUM_OF_SESSION_TYPES_REQUESTED
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AccessCheckoutClientImplTest {

    private val lifecycleOwnerMock = mock(LifecycleOwner::class.java)
    private val lifecycleMock = mock(Lifecycle::class.java)
    private val contextMock = mock(Context::class.java)
    private val localBroadcastManagerFactoryMock = mock(LocalBroadcastManagerFactory::class.java)
    private val localBroadcastManagerMock = mock(LocalBroadcastManager::class.java)

    private val sessionTokenRequestHandlerMock = mock(PaymentsCvcSessionRequestHandler::class.java)
    private val verifiedTokenRequestHandlerMock = mock(VerifiedTokensSessionRequestHandler::class.java)
    private val tokenHandlerFactoryMock = mock(SessionRequestHandlerFactory::class.java)
    private val activityLifecycleEventHandlerFactory = mock(ActivityLifecycleObserverInitialiser::class.java)

    @Before
    fun setup() {
        given(lifecycleOwnerMock.lifecycle).willReturn(lifecycleMock)
        val handlers = listOf(sessionTokenRequestHandlerMock, verifiedTokenRequestHandlerMock)
        given(tokenHandlerFactoryMock.getTokenHandlers()).willReturn(handlers)
        given(localBroadcastManagerFactoryMock.createInstance()).willReturn(localBroadcastManagerMock)
    }


    @Test
    fun `given AccessCheckoutClient is initialised with mandatory arguments then an instance of SDK is returned`() {
        val accessCheckoutClient =
            AccessCheckoutClientImpl(
                tokenHandlerFactoryMock,
                activityLifecycleEventHandlerFactory,
                localBroadcastManagerFactoryMock,
                contextMock
            )
        assertNotNull(accessCheckoutClient)
    }

    @Test
    fun `should send out an initialise broadcast with the request session type information before generating the session`() {
        val accessCheckoutClient =
            AccessCheckoutClientImpl(
                tokenHandlerFactoryMock,
                activityLifecycleEventHandlerFactory,
                localBroadcastManagerFactoryMock,
                contextMock
            )

        val cardDetails = CardDetails.Builder()
            .pan("1234")
            .expiryDate(12, 2020)
            .cvv("123")
            .build()

        val argument = ArgumentCaptor.forClass(Intent::class.java)

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION, PAYMENTS_CVC_SESSION))

        verify(localBroadcastManagerMock).sendBroadcast(argument.capture())

        assertEquals(NUM_OF_SESSION_TYPES_REQUESTED, argument.value.action as String)
        assertEquals(2, argument.value.getIntExtra("num_of_session_types", 0))

        assertEquals(1, argument.value.extras?.size())
    }

    @Test
    fun `should retrieve activity lifecycle event handler when client is created`() {
        AccessCheckoutClientImpl(
            tokenHandlerFactoryMock,
            activityLifecycleEventHandlerFactory,
            localBroadcastManagerFactoryMock,
            contextMock
        )

        verify(activityLifecycleEventHandlerFactory).initialise()
    }


    @Test
    fun `should be able to call each handler's canHandle method when calling generate`() {
        val cardDetails = CardDetails.Builder()
            .pan("1234")
            .expiryDate(12, 2020)
            .cvv("123")
            .build()

        val tokenRequests = listOf(VERIFIED_TOKEN_SESSION, PAYMENTS_CVC_SESSION)

        val accessCheckoutClient =
            AccessCheckoutClientImpl(
                tokenHandlerFactoryMock,
                activityLifecycleEventHandlerFactory,
                localBroadcastManagerFactoryMock,
                contextMock
            )

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

        val accessCheckoutClient =
            AccessCheckoutClientImpl(
                tokenHandlerFactoryMock,
                activityLifecycleEventHandlerFactory,
                localBroadcastManagerFactoryMock,
                contextMock
            )

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

        val accessCheckoutClient =
            AccessCheckoutClientImpl(
                tokenHandlerFactoryMock,
                activityLifecycleEventHandlerFactory,
                localBroadcastManagerFactoryMock,
                contextMock
            )

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

        val accessCheckoutClient =
            AccessCheckoutClientImpl(
                tokenHandlerFactoryMock,
                activityLifecycleEventHandlerFactory,
                localBroadcastManagerFactoryMock,
                contextMock
            )

        accessCheckoutClient.generateSession(cardDetails, emptyList())

        verify(verifiedTokenRequestHandlerMock, never()).handle(cardDetails)
        verify(sessionTokenRequestHandlerMock, never()).handle(cardDetails)
    }

}