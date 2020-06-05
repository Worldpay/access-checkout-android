package com.worldpay.access.checkout.validation.result

import java.util.concurrent.atomic.AtomicBoolean

class ValidationStateManager {

    val panValidated = AtomicBoolean(false)
    val monthValidated = AtomicBoolean(false)
    val yearValidated = AtomicBoolean(false)
    val cvvValidated = AtomicBoolean(false)

    fun isAllValid(): Boolean {
        return panValidated.get() && monthValidated.get() && yearValidated.get() && cvvValidated.get()
    }


}