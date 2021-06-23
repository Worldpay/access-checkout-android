package com.worldpay.access.checkout.client.testutil

import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.client.validation.model.CardBrand

class CardValidationListener : AccessCheckoutCardValidationListener {

    override fun onCvcValidated(isValid: Boolean) {}

    override fun onValidationSuccess() {}

    override fun onPanValidated(isValid: Boolean) {}

    override fun onBrandChange(cardBrand: CardBrand?) {}

    override fun onExpiryDateValidated(isValid: Boolean) {}
}
