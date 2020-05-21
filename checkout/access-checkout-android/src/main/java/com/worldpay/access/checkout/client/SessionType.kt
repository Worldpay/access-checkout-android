package com.worldpay.access.checkout.client

import java.io.Serializable

/**
 * An enum containing possible session types that can be requested
 */
enum class SessionType: Serializable {

    PAYMENTS_CVC_SESSION,
    VERIFIED_TOKEN_SESSION

}