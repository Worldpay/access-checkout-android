package com.worldpay.access.checkout.views

import com.worldpay.access.checkout.api.AccessCheckoutException

interface SessionResponseListener {
    fun onRequestStarted()
    fun onRequestFinished(sessionReference: String?, error: AccessCheckoutException?)
}

