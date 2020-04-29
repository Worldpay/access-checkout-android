package com.worldpay.access.checkout.client

enum class SessionType(val value: String) {

    PAYMENTS_CVC_SESSION("payments-cvc-session"),
    VERIFIED_TOKEN_SESSION("verified-token-session")

}