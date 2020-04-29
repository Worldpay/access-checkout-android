package com.worldpay.access.checkout.views

import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.client.SessionType

/**
 * The [SessionResponseListener] will receive an update once the session state has been generated. The listener will also
 * be notified of any errors that could have occurred during the request.
 */
interface SessionResponseListener {

    /**
     * Method for being notified when the request for the session state starts
     */
    fun onRequestStarted()

    /**
     * Method for being notified when the session state is available, or an error has occurred when trying to generate one
     * @param sessionResponseMap (Optional) when the session has been created successfully
     * @param error (Optional) a subclass of [AccessCheckoutException] when there has been an error generating the session state
     */
    fun onRequestFinished(sessionResponseMap: Map<SessionType, String>?, error: AccessCheckoutException?)
}

