package com.worldpay.access.checkout.util

internal object PropertyValidationUtil {

    fun validateNotNull(property: Any?, propertyKey: String) {
        if (property == null) {
            throw IllegalArgumentException("Expected $propertyKey to be provided but was not")
        }
    }

}
