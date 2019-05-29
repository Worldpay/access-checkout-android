package com.worldpay.access.checkout.views

import com.worldpay.access.checkout.api.AccessCheckoutException

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
     * @param (Optional) sessionState when the session has been created successfully
     * @param (Optional) [AccessCheckoutException] when there has been an error generating the session state
     */
    fun onRequestFinished(sessionState: String?, error: AccessCheckoutException?)
}

