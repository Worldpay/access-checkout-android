package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.InputFilter
import com.worldpay.access.checkout.validation.result.ExpiryYearValidationResultHandler
import com.worldpay.access.checkout.validation.validators.DateValidator

internal class ExpiryYearTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private val dateValidator: DateValidator,
    private val inputFilter: InputFilter,
    private val expiryYearValidationResultHandler: ExpiryYearValidationResultHandler,
    private val expiryYearEditText: EditText
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(year: Editable?) {
        val cardValidationRule = dateValidator.getValidationRule(cardConfiguration)
        inputFilter.filter(
            editText = expiryYearEditText,
            cardValidationRule = cardValidationRule.second
        )

        val result = dateValidator.validate(null, year.toString(), cardConfiguration)
        expiryYearValidationResultHandler.handleResult(
            validationResult = result
        )
    }

}
