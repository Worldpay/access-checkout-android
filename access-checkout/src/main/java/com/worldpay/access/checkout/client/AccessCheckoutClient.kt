package com.worldpay.access.checkout.client

import com.worldpay.access.checkout.client.session.model.CardDetails
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.client.validation.config.ValidationConfig

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
    val checkoutId: String
    val baseUrl: String
    /**
     * This function allows the generation of a new session for the client to use in the next phase
     * of the payment flow or other supported flow.
     *
     * The response of this function is asynchronous and will use a callback to respond back to the
     * client
     *
     * @param[cardDetails] Represents the [com.worldpay.access.checkout.client.session.model.CardDetails] that is provided by the customer
     * @param[sessionTypes] Represents a [List] of [com.worldpay.access.checkout.client.session.model.SessionType] that the client would like to retrieve
     */
    fun generateSessions(cardDetails: CardDetails, sessionTypes: List<SessionType>)

    /**
     * Initialises the card details input validation using the provided [ValidationConfig].
     *
     * This method sets up validation rules and logic for card input fields, ensuring that
     * user input is checked according to the configuration specified. It should be called
     * before any card details are processed to guarantee correct validation behaviour.
     *
     * @param validationConfiguration The [ValidationConfig] containing validation rules and settings.
     */
    fun initialiseValidation(validationConfiguration: ValidationConfig)

    /**
     * Releases resources and cleans up any state held by the client.
     *
     * This method should be called when the client is no longer needed to prevent memory leaks
     * and ensure proper shutdown of internal processes.
     */
    fun dispose()
}
