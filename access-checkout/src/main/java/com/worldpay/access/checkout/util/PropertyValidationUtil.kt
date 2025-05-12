package com.worldpay.access.checkout.util

import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException

internal object PropertyValidationUtil {

    fun validateNotNull(property: Any?, propertyKey: String) {
        if (property == null) {
            throw AccessCheckoutException("Expected $propertyKey to be provided but was not")
        }
    }
}
