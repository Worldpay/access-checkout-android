package com.worldpay.access.checkout.session.handlers

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.worldpay.access.checkout.client.session.model.CardDetails
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.client.session.model.SessionType.CVC
import com.worldpay.access.checkout.session.AccessCheckoutClientImpl
import com.worldpay.access.checkout.session.ActivityLifecycleObserverInitialiser
import com.worldpay.access.checkout.session.broadcast.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.session.broadcast.receivers.NUM_OF_SESSION_TYPES_REQUESTED
import com.worldpay.access.checkout.testutils.PlainRobolectricTestRunner
import kotlin.test.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

@RunWith(PlainRobolectricTestRunner::class)
class AccessCheckoutClientImplTest {

    private val lifecycleOwnerMock = mock(LifecycleOwner::class.java)
    private val lifecycleMock = mock(Lifecycle::class.java)
    private val contextMock = mock(Context::class.java)
    private val localBroadcastManagerFactoryMock = mock(LocalBroadcastManagerFactory::class.java)
    private val localBroadcastManagerMock = mock(LocalBroadcastManager::class.java)

    private val cvcSessionRequestHandlerMock = mock(CvcSessionRequestHandler::class.java)
    private val cardSessionRequestHandlerMock = mock(CardSessionRequestHandler::class.java)
    private val tokenHandlerFactoryMock = mock(SessionRequestHandlerFactory::class.java)
    private val activityLifecycleEventHandlerFactory = mock(ActivityLifecycleObserverInitialiser::class.java)

    @Before
    fun setup() {
        given(lifecycleOwnerMock.lifecycle).willReturn(lifecycleMock)
        val handlers = listOf(cvcSessionRequestHandlerMock, cardSessionRequestHandlerMock)
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
            .expiryDate("1220")
            .cvc("123")
            .build()

        val argument = ArgumentCaptor.forClass(Intent::class.java)

        accessCheckoutClient.generateSessions(cardDetails, listOf(CARD, CVC))

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
            .expiryDate("1220")
            .cvc("123")
            .build()

        val tokenRequests = listOf(CARD, CVC)

        val accessCheckoutClient =
            AccessCheckoutClientImpl(
                tokenHandlerFactoryMock,
                activityLifecycleEventHandlerFactory,
                localBroadcastManagerFactoryMock,
                contextMock
            )

        accessCheckoutClient.generateSessions(cardDetails, tokenRequests)

        verify(cvcSessionRequestHandlerMock).canHandle(tokenRequests)
        verify(cardSessionRequestHandlerMock).canHandle(tokenRequests)
    }

    @Test
    fun `should call handle method on the sessionTokenRequestHandler when calling generate for session token`() {
        val cardDetails = CardDetails.Builder()
            .cvc("123")
            .build()

        val tokenRequests = listOf(CVC)
        given(cvcSessionRequestHandlerMock.canHandle(tokenRequests)).willCallRealMethod()

        val accessCheckoutClient =
            AccessCheckoutClientImpl(
                tokenHandlerFactoryMock,
                activityLifecycleEventHandlerFactory,
                localBroadcastManagerFactoryMock,
                contextMock
            )

        accessCheckoutClient.generateSessions(cardDetails, tokenRequests)

        verify(cvcSessionRequestHandlerMock).canHandle(tokenRequests)
        verify(cvcSessionRequestHandlerMock).handle(cardDetails)
        verify(cardSessionRequestHandlerMock, never()).handle(cardDetails)
    }

    @Test
    fun `should call handle method on the cardSessionRequestHandler when calling generate for verified token`() {
        val cardDetails = CardDetails.Builder()
            .pan("1234")
            .expiryDate("1220")
            .cvc("123")
            .build()

        val tokenRequests = listOf(CARD)
        given(cardSessionRequestHandlerMock.canHandle(tokenRequests)).willCallRealMethod()

        val accessCheckoutClient =
            AccessCheckoutClientImpl(
                tokenHandlerFactoryMock,
                activityLifecycleEventHandlerFactory,
                localBroadcastManagerFactoryMock,
                contextMock
            )

        accessCheckoutClient.generateSessions(cardDetails, tokenRequests)

        verify(cardSessionRequestHandlerMock).canHandle(tokenRequests)
        verify(cardSessionRequestHandlerMock).handle(cardDetails)
        verify(cvcSessionRequestHandlerMock, never()).handle(cardDetails)
    }

    @Test
    fun `should not call handle when canHandle returns false`() {
        val cardDetails = CardDetails.Builder()
            .pan("1234")
            .expiryDate("1220")
            .cvc("123")
            .build()

        val accessCheckoutClient =
            AccessCheckoutClientImpl(
                tokenHandlerFactoryMock,
                activityLifecycleEventHandlerFactory,
                localBroadcastManagerFactoryMock,
                contextMock
            )

        accessCheckoutClient.generateSessions(cardDetails, emptyList())

        verify(cardSessionRequestHandlerMock, never()).handle(cardDetails)
        verify(cvcSessionRequestHandlerMock, never()).handle(cardDetails)
    }
}
