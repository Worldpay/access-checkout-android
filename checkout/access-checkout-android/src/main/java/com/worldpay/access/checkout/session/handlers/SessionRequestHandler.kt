package com.worldpay.access.checkout.session.handlers

import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.SessionType

/**
 * The [SessionRequestHandler] interface provides the contract that all session request handlers
 * should abide by when deciding whether the handler should be able to handle the given session type.
 */
internal interface SessionRequestHandler {

    /**
     * Checks to see if the given [sessionTypes] can be handled by the implemented handler
     *
     * @param sessionTypes A [List] of requested [SessionType]
     * @returns Boolean True if the given [sessionTypes] contains a [SessionType] that can be handled
     */
    fun canHandle(sessionTypes: List<SessionType>): Boolean

    /**
     * Handles the given [cardDetails]
     *
     * @param cardDetails the [CardDetails] to be processed
     */
    fun handle(cardDetails: CardDetails)

}
