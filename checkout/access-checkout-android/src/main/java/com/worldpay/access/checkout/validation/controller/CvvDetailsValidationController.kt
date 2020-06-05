package com.worldpay.access.checkout.validation.controller

import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CARD_DEFAULTS
import com.worldpay.access.checkout.validation.watchers.TextWatcherFactory

internal class CvvDetailsValidationController(
    cvvEditText: EditText,
    textWatcherFactory: TextWatcherFactory
) {

    init {
        val cardConfiguration = CardConfiguration(emptyList(), CARD_DEFAULTS)
        val cvvTextWatcher = textWatcherFactory.createCvvTextWatcher(cvvEditText, null, cardConfiguration)
        cvvEditText.addTextChangedListener(cvvTextWatcher)
    }

}
