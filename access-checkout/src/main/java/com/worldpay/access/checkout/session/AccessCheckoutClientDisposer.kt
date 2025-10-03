package com.worldpay.access.checkout.session

import com.worldpay.access.checkout.client.AccessCheckoutClient
import com.worldpay.access.checkout.client.AccessCheckoutClientImpl

open class AccessCheckoutClientDisposer {
    open fun dispose(accessCheckoutClient: AccessCheckoutClient) {
        (accessCheckoutClient as AccessCheckoutClientImpl).dispose()
    }
}
