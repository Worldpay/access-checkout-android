package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.InputFilter
import com.worldpay.access.checkout.validation.result.ExpiryMonthValidationResultHandler
import com.worldpay.access.checkout.validation.validators.DateValidator

internal class ExpiryMonthTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private val dateValidator: DateValidator,
    private val inputFilter: InputFilter,
    private val expiryMonthValidationResultHandler: ExpiryMonthValidationResultHandler,
    private val expiryMonthEditText: EditText
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(month: Editable?) {
        val cardValidationRule = dateValidator.getValidationRule(cardConfiguration)
        inputFilter.filter(
            editText = expiryMonthEditText,
            cardValidationRule = cardValidationRule.first
        )

        val result = dateValidator.validate(month.toString(), null, cardConfiguration)
        expiryMonthValidationResultHandler.handleResult(
            validationResult = result
        )
    }

}
