package com.worldpay.access.checkout.session.handlers

import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.SessionType

/**
 * An interface for handling requests for sessions
 */
internal interface SessionRequestHandler {

    /**
     * Accepts a list of [SessionType]s and returns a Boolean value for whether the list contains a type of session it can handle
     */
    fun canHandle(sessionTypes: List<SessionType>): Boolean

    /**
     * Takes in [CardDetails] and handles the request
     */
    fun handle(cardDetails: CardDetails)

}
