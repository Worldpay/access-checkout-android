package com.worldpay.access.checkout.session

import com.worldpay.access.checkout.client.session.AccessCheckoutClient

open class AccessCheckoutClientDisposer {
    open fun dispose(accessCheckoutClient: AccessCheckoutClient) {
        (accessCheckoutClient as AccessCheckoutClientImpl).dispose()
    }
}
