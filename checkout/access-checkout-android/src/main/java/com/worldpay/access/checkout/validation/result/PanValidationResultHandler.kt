package com.worldpay.access.checkout.validation.result

import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.client.validation.AccessCheckoutPanValidationListener

class PanValidationResultHandler(
    private val validationListener: AccessCheckoutPanValidationListener,
    private val validationStateManager: ValidationStateManager
) {

    fun handleResult(isValid: Boolean, cardBrand: CardBrand?) {
        validationListener.onPanValidated(cardBrand, isValid)
        validationStateManager.panValidated.set(isValid)

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }
    }

}