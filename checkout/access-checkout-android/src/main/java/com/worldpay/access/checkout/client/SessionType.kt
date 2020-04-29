package com.worldpay.access.checkout.client

import java.io.Serializable

enum class SessionType: Serializable {

    PAYMENTS_CVC_SESSION,
    VERIFIED_TOKEN_SESSION

}