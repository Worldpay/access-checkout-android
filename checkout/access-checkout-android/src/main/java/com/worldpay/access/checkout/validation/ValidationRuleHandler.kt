package com.worldpay.access.checkout.validation

import android.text.InputFilter
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.validation.card.CardDetailComponents
import com.worldpay.access.checkout.validation.card.CardDetailType
import com.worldpay.access.checkout.validation.card.CardDetailType.*
import com.worldpay.access.checkout.validation.card.CardDetailType.CVV

class ValidationRuleHandler(private val cardDetailComponents: CardDetailComponents) {

    private val defaultMaxLength = 100

    fun handle(cardDetailType: CardDetailType, cardValidationRule: CardValidationRule) {
        if (cardDetailType == PAN) {
            setInputLengthFilter(cardDetailComponents.pan, cardValidationRule.validLengths.max())
        }

        if (cardDetailType == EXPIRY_MONTH) {
            setInputLengthFilter(cardDetailComponents.expiryMonth, cardValidationRule.validLengths.max())
        }

        if (cardDetailType == EXPIRY_YEAR) {
            setInputLengthFilter(cardDetailComponents.expiryYear, cardValidationRule.validLengths.max())
        }

        if (cardDetailType == CVV) {
            setInputLengthFilter(cardDetailComponents.cvv, cardValidationRule.validLengths.max())
        }
    }

    private fun setInputLengthFilter(editText: EditText, maxLength: Int?) {
        editText.filters += InputFilter.LengthFilter(maxLength ?: defaultMaxLength)
    }

}