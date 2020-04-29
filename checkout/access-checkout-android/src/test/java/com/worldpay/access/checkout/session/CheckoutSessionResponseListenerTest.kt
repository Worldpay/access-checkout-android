package com.worldpay.access.checkout.session

import com.nhaarman.mockitokotlin2.then
import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
import com.worldpay.access.checkout.views.SessionResponseListener
import org.junit.Test
import org.mockito.Mockito.mock

class CheckoutSessionResponseListenerTest {

    private val externalListener: SessionResponseListener = mock(SessionResponseListener::class.java)
    private val tag: String = "some-tag"
    private val checkoutSessionResponseListener: CheckoutSessionResponseListener =
        CheckoutSessionResponseListener(
            tag,
            externalListener
        )

    @Test
    fun `given a call back is made with non-empty session response then checkout session response listener should notify external session response listener`() {
        val sessionResponseMap = mapOf(VERIFIED_TOKEN_SESSION to "some reference")

        checkoutSessionResponseListener.onRequestFinished(sessionResponseMap, null)

        then(externalListener)
            .should()
            .onRequestFinished(sessionResponseMap, null)
    }


    @Test
    fun `given a call back is made with empty session response then checkout session response listener should notify external session response listener`() {
        val accessCheckoutError = AccessCheckoutException.AccessCheckoutError("some error")
        checkoutSessionResponseListener.onRequestFinished(null, accessCheckoutError)

        then(externalListener)
            .should()
            .onRequestFinished(null, accessCheckoutError)
    }

    @Test
    fun `given a call back is made to notify that request has started then checkout session response listener should not notify anyone`() {
        checkoutSessionResponseListener.onRequestStarted()

        then(externalListener)
            .shouldHaveZeroInteractions()
    }
}