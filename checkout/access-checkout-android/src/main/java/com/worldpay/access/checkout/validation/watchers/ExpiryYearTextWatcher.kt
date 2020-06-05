package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.InputFilterHandler
import com.worldpay.access.checkout.validation.ValidationResultHandler
import com.worldpay.access.checkout.validation.validators.DateValidator

internal class ExpiryYearTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private val dateValidator: DateValidator,
    private val inputFilterHandler: InputFilterHandler = InputFilterHandler(),
    private val validationResultHandler: ValidationResultHandler,
    private val expiryYearEditText: EditText
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(year: Editable?) {
        val cardValidationRule = dateValidator.getValidationRule(cardConfiguration)
        inputFilterHandler.handle(
            editText = expiryYearEditText,
            cardValidationRule = cardValidationRule.second
        )

        val result = dateValidator.validate(null, year.toString(), cardConfiguration)
        validationResultHandler.handleExpiryYearValidationResult(
            validationResult = result
        )
    }

}