package com.worldpay.access.checkout.client.session.model

import java.io.Serializable

/**
 * An enum containing possible session types that can be requested
 */
enum class SessionType: Serializable {

    PAYMENTS_CVC,
    VERIFIED_TOKENS

}
