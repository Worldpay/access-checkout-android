package com.worldpay.access.checkout

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.support.v4.content.LocalBroadcastManager
import com.google.common.base.CaseFormat
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.api.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.views.SessionResponseListener
import com.nhaarman.mockitokotlin2.any
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.*
import org.mockito.internal.util.reflection.FieldSetter.setField


class AccessCheckoutClientTest {

    private val context = mock(Context::class.java)
    private val sessionResponseListener = mock(SessionResponseListener::class.java)
    private val lifecycleOwner = mock(LifecycleOwner::class.java)
    private val lifecycle = mock(Lifecycle::class.java)
    private val merchantId = "merchant-123"
    private val baseUrl = "http://localhost"

    private lateinit var accessCheckoutClient: AccessCheckoutClient


    @Before
    fun setup() {
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)
        accessCheckoutClient = AccessCheckoutClient.init(baseUrl, merchantId, sessionResponseListener, context, lifecycleOwner)
    }

    @Test
    fun `given AccessCheckoutClient is initialised with mandatory arguments then an instance of SDK is returned`() {
        val accessCheckoutClient = AccessCheckoutClient.init(baseUrl, merchantId, sessionResponseListener, context, lifecycleOwner)

        assertNotNull(accessCheckoutClient)

        verify(lifecycle).addObserver(accessCheckoutClient)
    }

    @Test
    fun `given the user requests a session reference then the session request service is started`() {
        val cardNumber = "1234"
        val month = 12
        val year = 2020
        val cvv = "123"

        accessCheckoutClient.generateSessionState(cardNumber, month, year, cvv)

        then(sessionResponseListener)
            .should()
            .onRequestStarted()

        then(context)
            .should()
            .startService(any())
    }

    @Test
    fun `given a call back is made with non-empty session response then AccessCheckoutClient should notify session response listener`() {
        val sessionReference = "some reference"

        accessCheckoutClient.onRequestFinished(sessionReference, null)

        then(sessionResponseListener)
            .should()
            .onRequestFinished(sessionReference, null)
    }

    @Test
    fun `given a call back is made with empty session response then AccessCheckoutClient should notify session response listener`() {
        val accessCheckoutError = AccessCheckoutException.AccessCheckoutError("some error")
        accessCheckoutClient.onRequestFinished(null, accessCheckoutError)

        then(sessionResponseListener)
            .should()
            .onRequestFinished(null, accessCheckoutError)
    }

    @Test
    fun `given a call back is made to notify that request has started then AccessCheckoutClient should not notify anyone`() {
        accessCheckoutClient.onRequestStarted()

        then(sessionResponseListener)
            .shouldHaveZeroInteractions()
    }

    @Test
    fun `given AccessCheckoutClient has been triggered on resume, then broadcast manager has session request receiver registered`() {
        val localBroadcastManager = mock(LocalBroadcastManager::class.java)
        val localBroadcastManagerFactory = mock(LocalBroadcastManagerFactory::class.java)
        setMock(localBroadcastManager)
        setMock(localBroadcastManagerFactory)
        given(localBroadcastManagerFactory.createInstance()).willReturn(localBroadcastManager)

        accessCheckoutClient.startListener()

        verify(localBroadcastManager).registerReceiver(any(), any())
    }

    @Test
    fun `given AccessCheckoutClient has been triggered on stop, then broadcast manager has session request receiver unregistered`() {
        val localBroadcastManager = mock(LocalBroadcastManager::class.java)
        setMock(localBroadcastManager)

        accessCheckoutClient.disconnectListener()

        verify(localBroadcastManager).unregisterReceiver(any())
    }

    @Test
    fun `given AccessCheckoutClient has been triggered on stop, then AccessCheckoutClient is removed as an observer of the activity lifecycle`() {
        accessCheckoutClient.removeObserver()

        verify(lifecycle).removeObserver(accessCheckoutClient)
    }


    private fun setMock(clazz: Any) {
        setField(accessCheckoutClient, accessCheckoutClient.javaClass.getDeclaredField(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, clazz::class.simpleName!!)), clazz)
    }

}