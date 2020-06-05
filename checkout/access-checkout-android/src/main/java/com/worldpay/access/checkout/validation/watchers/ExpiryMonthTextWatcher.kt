package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.InputFilterHandler
import com.worldpay.access.checkout.validation.result.ValidationResultHandler
import com.worldpay.access.checkout.validation.validators.DateValidator

internal class ExpiryMonthTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private val dateValidator: DateValidator,
    private val inputFilterHandler: InputFilterHandler = InputFilterHandler(),
    private val validationResultHandler: ValidationResultHandler,
    private val expiryMonthEditText: EditText
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(month: Editable?) {
        val cardValidationRule = dateValidator.getValidationRule(cardConfiguration)
        inputFilterHandler.handle(
            editText = expiryMonthEditText,
            cardValidationRule = cardValidationRule.first
        )

        val result = dateValidator.validate(month.toString(), null, cardConfiguration)
        validationResultHandler.handleExpiryMonthValidationResult(
            validationResult = result
        )
    }

}