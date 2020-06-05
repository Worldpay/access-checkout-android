package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.InputFilterHandler
import com.worldpay.access.checkout.validation.ValidationResultHandler
import com.worldpay.access.checkout.validation.validators.PANValidator

internal class PANTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private var panValidator: PANValidator,
    private val inputFilterHandler: InputFilterHandler = InputFilterHandler(),
    private val panEditText: EditText,
    private val validationResultHandler: ValidationResultHandler
) : AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(pan: Editable?) {
        val cardValidationRule = panValidator.getValidationRule(pan.toString(), cardConfiguration)
        inputFilterHandler.handle(
            editText = panEditText,
            cardValidationRule = cardValidationRule
        )

        val result = panValidator.validate(pan.toString(), cardConfiguration)
        validationResultHandler.handlePanValidationResult(
            validationResult = result.first,
            cardBrand = result.second
        )
    }

}