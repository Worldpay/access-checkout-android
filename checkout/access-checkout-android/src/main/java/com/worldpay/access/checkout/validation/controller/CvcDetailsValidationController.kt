package com.worldpay.access.checkout.validation.controller

import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CARD_DEFAULTS
import com.worldpay.access.checkout.validation.decorators.CvcFieldDecorator

internal class CvcDetailsValidationController(
    cvcFieldDecorator : CvcFieldDecorator
) {

    init {
        val cardConfiguration = CardConfiguration(emptyList(), CARD_DEFAULTS)
        cvcFieldDecorator.decorate(cardConfiguration)
    }

}
