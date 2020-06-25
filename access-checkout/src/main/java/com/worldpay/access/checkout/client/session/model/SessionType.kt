package com.worldpay.access.checkout.client.session.model

import java.io.Serializable

/**
 * An enum containing possible session types that can be requested
 */
enum class SessionType: Serializable {

    /**
     * Payments cvc session type that represents a cvc session
     * This token can be further used with the Worldpay payments service.
     */
    PAYMENTS_CVC,

    /**
     * Verified token session type that represents a card session
     * This token can be further used with the Worldpay verified token service.
     */
    VERIFIED_TOKENS

}
