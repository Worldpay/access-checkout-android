package com.worldpay.access.checkout.session.handlers

import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.SessionType

/**
 * The [SessionRequestHandler] interface provides the contract that all session request handlers
 * should abide by when deciding whether the handler should be able to handle the given session type.
 */
internal interface SessionRequestHandler {

    /**
     * @param sessionTypes - a list of requested [SessionType]s
     * @returns Boolean  - if the list contains a [SessionType] that can be handled
     */
    fun canHandle(sessionTypes: List<SessionType>): Boolean

    /**
     * @param cardDetails
     */
    fun handle(cardDetails: CardDetails)

}
