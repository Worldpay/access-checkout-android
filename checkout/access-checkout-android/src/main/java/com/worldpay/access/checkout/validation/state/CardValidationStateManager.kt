package com.worldpay.access.checkout.validation.state

import java.util.concurrent.atomic.AtomicBoolean

class CardValidationStateManager: ExpiryDateFieldValidationStateManager,
    CvcFieldValidationStateManager,
    PanFieldValidationStateManager {

    override val panValidated = AtomicBoolean(false)
    override val expiryDateValidated = AtomicBoolean(false)
    override val cvvValidated = AtomicBoolean(false)

    override fun isAllValid(): Boolean {
        return panValidated.get() && expiryDateValidated.get() && cvvValidated.get()
    }

}
