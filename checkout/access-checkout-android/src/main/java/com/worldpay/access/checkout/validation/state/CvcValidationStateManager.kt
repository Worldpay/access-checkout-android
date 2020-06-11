package com.worldpay.access.checkout.validation.state

import java.util.concurrent.atomic.AtomicBoolean

class CvcValidationStateManager:
    CvcFieldValidationStateManager {
    override val cvvValidated = AtomicBoolean(false)

    override fun isAllValid(): Boolean {
        return cvvValidated.get()
    }
}