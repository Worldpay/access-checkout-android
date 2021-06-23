package com.worldpay.access.checkout.client.session.model

import java.io.Serializable

/**
 * An enum containing possible session types that can be requested
 */
enum class SessionType : Serializable {

    /**
     * Session type that represents a session for the cvc details
     * This token can be further used with the Worldpay payments service.
     */
    CVC,

    /**
     * Session type that represents a session for card details
     * This token can be further used with the Worldpay verified token service.
     */
    CARD
}
