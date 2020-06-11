package com.worldpay.access.checkout.validation.state

import java.util.concurrent.atomic.AtomicBoolean

interface FieldValidationStateManager {
    fun isAllValid(): Boolean
}

interface ExpiryDateFieldValidationStateManager :
    FieldValidationStateManager {
    val expiryDateValidated : AtomicBoolean
}

interface PanFieldValidationStateManager :
    FieldValidationStateManager {
    val panValidated : AtomicBoolean
}

interface CvcFieldValidationStateManager :
    FieldValidationStateManager {
    val cvvValidated : AtomicBoolean
}