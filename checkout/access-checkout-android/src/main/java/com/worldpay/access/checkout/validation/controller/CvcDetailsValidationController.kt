package com.worldpay.access.checkout.validation.controller

import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CARD_DEFAULTS

internal class CvcDetailsValidationController(
    cvcEditText: EditText,
    fieldDecoratorFactory: FieldDecoratorFactory
) {

    init {
        val cardConfiguration = CardConfiguration(emptyList(), CARD_DEFAULTS)
        fieldDecoratorFactory.decorateCvcField(cvcEditText, null, cardConfiguration)
    }

}
