package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.validation.NewValidatorUtils.getValidationResultFor

class NewCVVValidator {
    fun validate(cvv: String, validationRule: CardValidationRule): Boolean {
        if (cvv.isBlank()) {
            return false
        }

        return getValidationResultFor(cvv, validationRule)
    }
}