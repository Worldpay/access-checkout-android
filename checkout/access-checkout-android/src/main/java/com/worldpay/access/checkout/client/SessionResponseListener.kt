package com.worldpay.access.checkout.client

import com.worldpay.access.checkout.api.AccessCheckoutException

/**
 * The [SessionResponseListener] will receive an update once the session state has been generated. The listener will also
 * be notified of any errors that could have occurred during the request.
 */
interface SessionResponseListener {

    /**
     * Function is called when the session response has been successfully retrieved.
     *
     * @param[sessionResponseMap] A [Map] of [SessionType] requested with associated [String] response
     */
    fun onSuccess(sessionResponseMap: Map<SessionType, String>)

    /**
     * Function is called when the session request errors
     *
     * @param[error] The [AccessCheckoutException] that was raised
     */
    fun onError(error: AccessCheckoutException)

}

