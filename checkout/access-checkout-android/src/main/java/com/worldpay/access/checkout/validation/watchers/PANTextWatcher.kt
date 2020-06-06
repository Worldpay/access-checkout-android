package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.InputFilter
import com.worldpay.access.checkout.validation.result.PanValidationResultHandler
import com.worldpay.access.checkout.validation.validators.NewPANValidator

internal class PANTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private var panValidator: NewPANValidator,
    private val inputFilter: InputFilter = InputFilter(),
    private val panEditText: EditText,
    private val panValidationResultHandler: PanValidationResultHandler
) : AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(pan: Editable?) {
        val cardValidationRule = panValidator.getValidationRule(pan.toString(), cardConfiguration)
        inputFilter.filter(
            editText = panEditText,
            cardValidationRule = cardValidationRule
        )

        val result = panValidator.validate(pan.toString(), cardConfiguration)
        panValidationResultHandler.handleResult(
            isValid = result.first,
            cardBrand = result.second
        )
    }

}
