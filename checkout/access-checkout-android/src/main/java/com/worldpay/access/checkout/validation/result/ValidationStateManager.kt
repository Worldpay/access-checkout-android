package com.worldpay.access.checkout.validation.result

import java.util.concurrent.atomic.AtomicBoolean

class ValidationStateManager {

    val panValidated = AtomicBoolean(false)
    val expiryDateValidated = AtomicBoolean(false)
    val cvvValidated = AtomicBoolean(false)

    fun isAllValid(): Boolean {
        return panValidated.get() && expiryDateValidated.get() && cvvValidated.get()
    }

}
