package com.worldpay.access.checkout.validation

import android.text.InputFilter
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardValidationRule

class InputFilter {

    private val defaultMaxLength = 100

    fun filter(editText: EditText, cardValidationRule: CardValidationRule) {
        val maxLength = cardValidationRule.validLengths.max() ?: defaultMaxLength
        editText.filters += InputFilter.LengthFilter(maxLength)
    }

}
