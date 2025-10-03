package com.worldpay.access.checkout.client

internal class AccessCheckoutClientDisposer {
    internal fun dispose(accessCheckoutClient: AccessCheckoutClient) {
        (accessCheckoutClient as AccessCheckoutClientImpl).dispose()
    }
}
