package com.worldpay.access.checkout.client

internal class AccessCheckoutClientDisposer {
    internal fun initialise(accessCheckoutClient: AccessCheckoutClient) {
        (accessCheckoutClient as AccessCheckoutClientImpl).dispose()
    }
}
