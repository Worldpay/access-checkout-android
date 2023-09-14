package com.worldpay.access.checkout.client.session

import com.worldpay.access.checkout.client.session.model.CardDetails
import com.worldpay.access.checkout.client.session.model.SessionType

/**
 * This interface is the entry point to the session generate for Access Worldpay Services.
 *
 * This interface should be used when client wishes to generate a new session to initiate a payment
 * flow or other supported flow.
 *
 * An implementation of this interface is returned after the [AccessCheckoutClientBuilder] is used.
 * This is the only way to create the instance of the implementation class.
 */
interface AccessCheckoutClient {

    /**
     * This function allows the generation of a new session for the client to use in the next phase
     * of the payment flow or other supported flow.
     *
     * The response of this function is asynchronous and will use a callback to respond back to the
     * client
     *
     * @param[cardDetails] Represents the [CardDetails] that is provided by the customer
     * @param[sessionTypes] Represents a [List] of [SessionType] that the client would like to retrieve
     */
    fun generateSessions(cardDetails: CardDetails, sessionTypes: List<SessionType>)
}
