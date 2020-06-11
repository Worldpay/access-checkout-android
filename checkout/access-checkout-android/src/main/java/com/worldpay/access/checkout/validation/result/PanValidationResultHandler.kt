package com.worldpay.access.checkout.validation.result

import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutPanValidationListener
import com.worldpay.access.checkout.client.validation.model.CardBrand
import com.worldpay.access.checkout.validation.state.PanFieldValidationStateManager

class PanValidationResultHandler(
    private val validationListener: AccessCheckoutPanValidationListener,
    private val validationStateManager: PanFieldValidationStateManager
) {

    fun handleResult(isValid: Boolean, cardBrand: CardBrand?) {
        validationListener.onPanValidated(cardBrand, isValid)
        validationStateManager.panValidated = isValid

        if (validationStateManager.isAllValid()) {
            validationListener.onValidationSuccess()
        }
    }

}
