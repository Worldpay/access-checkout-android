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
import com.worldpay.access.checkout.session.ActivityLifecycleObserver
import com.worldpay.access.checkout.session.ActivityLifecycleObserverInitialiser
import com.worldpay.access.checkout.session.broadcast.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.session.broadcast.receivers.NUM_OF_SESSION_TYPES_REQUESTED
import com.worldpay.access.checkout.testutils.PlainRobolectricTestRunner
import com.worldpay.access.checkout.testutils.createAccessEditTextMock
import com.worldpay.access.checkout.ui.AccessEditText
import kotlin.test.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.*

@RunWith(PlainRobolectricTestRunner::class)
class AccessCheckoutClientImplTest {

    private val lifecycleOwnerMock = mock<LifecycleOwner>()
    private val lifecycleMock = mock<Lifecycle>()
    private val contextMock = mock<Context>()
    private val localBroadcastManagerFactoryMock = mock<LocalBroadcastManagerFactory>()
    private val localBroadcastManagerMock = mock<LocalBroadcastManager>()

    private val cvcSessionRequestHandlerMock = mock<CvcSessionRequestHandler>()
    private val cardSessionRequestHandlerMock = mock<CardSessionRequestHandler>()
    private val tokenHandlerFactoryMock = mock<SessionRequestHandlerFactory>()
    private val activityLifecycleObserverInitialiser =
        mock<ActivityLifecycleObserverInitialiser>()

    @Before
    fun setup() {
        given(lifecycleOwnerMock.lifecycle).willReturn(lifecycleMock)
        val handlers = listOf(cvcSessionRequestHandlerMock, cardSessionRequestHandlerMock)
        given(tokenHandlerFactoryMock.getTokenHandlers()).willReturn(handlers)
        given(localBroadcastManagerFactoryMock.createInstance()).willReturn(
            localBroadcastManagerMock
        )
    }

    @Test
    fun `given AccessCheckoutClient is initialised with mandatory arguments then an instance of SDK is returned`() {
        val accessCheckoutClient =
            AccessCheckoutClientImpl(
                tokenHandlerFactoryMock,
                activityLifecycleObserverInitialiser,
                localBroadcastManagerFactoryMock,
                contextMock
            )
        assertNotNull(accessCheckoutClient)
    }

    @Test
    fun `should send out an initialise broadcast with the request session type information before generating the session`() {
        val pan = createAccessEditTextMock("1234")
        val expiryDate = createAccessEditTextMock("1120")
        val cvc = createAccessEditTextMock("123")

        val accessCheckoutClient =
            AccessCheckoutClientImpl(
                tokenHandlerFactoryMock,
                activityLifecycleObserverInitialiser,
                localBroadcastManagerFactoryMock,
                contextMock
            )

        val cardDetails = CardDetails.Builder()
            .pan(pan)
            .expiryDate(expiryDate)
            .cvc(cvc)
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
            activityLifecycleObserverInitialiser,
            localBroadcastManagerFactoryMock,
            contextMock
        )

        verify(activityLifecycleObserverInitialiser).initialise()
    }

    @Test
    fun `should be able to call each handler's canHandle method when calling generate`() {
        val pan = mock<AccessEditText>()
        whenever(pan.text).thenReturn("1234")
        val expiryDate = mock<AccessEditText>()
        whenever(expiryDate.text).thenReturn("1120")
        val cvc = mock<AccessEditText>()
        whenever(cvc.text).thenReturn("123")

        val cardDetails = CardDetails.Builder()
            .pan(pan)
            .expiryDate(expiryDate)
            .cvc(cvc)
            .build()

        val tokenRequests = listOf(CARD, CVC)

        val accessCheckoutClient =
            AccessCheckoutClientImpl(
                tokenHandlerFactoryMock,
                activityLifecycleObserverInitialiser,
                localBroadcastManagerFactoryMock,
                contextMock
            )

        accessCheckoutClient.generateSessions(cardDetails, tokenRequests)

        verify(cvcSessionRequestHandlerMock).canHandle(tokenRequests)
        verify(cardSessionRequestHandlerMock).canHandle(tokenRequests)
    }

    @Test
    fun `should call handle method on the sessionTokenRequestHandler when calling generate for session token`() {
        val cvc = mock<AccessEditText>()
        whenever(cvc.text).thenReturn("123")

        val cardDetails = CardDetails.Builder()
            .cvc(cvc)
            .build()

        val tokenRequests = listOf(CVC)
        given(cvcSessionRequestHandlerMock.canHandle(tokenRequests)).willCallRealMethod()

        val accessCheckoutClient =
            AccessCheckoutClientImpl(
                tokenHandlerFactoryMock,
                activityLifecycleObserverInitialiser,
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
        val pan = mock<AccessEditText>()
        whenever(pan.text).thenReturn("1234")
        val expiryDate = mock<AccessEditText>()
        whenever(expiryDate.text).thenReturn("1120")
        val cvc = mock<AccessEditText>()
        whenever(cvc.text).thenReturn("123")

        val cardDetails = CardDetails.Builder()
            .pan(pan)
            .expiryDate(expiryDate)
            .cvc(cvc)
            .build()

        val tokenRequests = listOf(CARD)
        given(cardSessionRequestHandlerMock.canHandle(tokenRequests)).willCallRealMethod()

        val accessCheckoutClient =
            AccessCheckoutClientImpl(
                tokenHandlerFactoryMock,
                activityLifecycleObserverInitialiser,
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
        val pan = mock<AccessEditText>()
        whenever(pan.text).thenReturn("1234")
        val expiryDate = mock<AccessEditText>()
        whenever(expiryDate.text).thenReturn("1120")
        val cvc = mock<AccessEditText>()
        whenever(cvc.text).thenReturn("123")

        val cardDetails = CardDetails.Builder()
            .pan(pan)
            .expiryDate(expiryDate)
            .cvc(cvc)
            .build()

        val accessCheckoutClient =
            AccessCheckoutClientImpl(
                tokenHandlerFactoryMock,
                activityLifecycleObserverInitialiser,
                localBroadcastManagerFactoryMock,
                contextMock
            )

        accessCheckoutClient.generateSessions(cardDetails, emptyList())

        verify(cardSessionRequestHandlerMock, never()).handle(cardDetails)
        verify(cvcSessionRequestHandlerMock, never()).handle(cardDetails)
    }

    @Test
    fun `should call activityLifeCycleObserver onStop() when dispose() is called`() {
        val activityLifecycleObserver = mock<ActivityLifecycleObserver>()
        given(activityLifecycleObserverInitialiser.initialise()).willReturn(
            activityLifecycleObserver
        )
        val accessCheckoutClient = AccessCheckoutClientImpl(
            tokenHandlerFactoryMock,
            activityLifecycleObserverInitialiser,
            localBroadcastManagerFactoryMock,
            contextMock
        )

        accessCheckoutClient.dispose()

        verify(activityLifecycleObserver).onStop()
    }
}
