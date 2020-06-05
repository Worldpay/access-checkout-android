package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.InputFilterHandler
import com.worldpay.access.checkout.validation.ValidationResultHandler
import com.worldpay.access.checkout.validation.card.CardDetailType.CVV
import com.worldpay.access.checkout.validation.validators.CVVValidator

internal class CVVTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private val panEditText: EditText,
    private val cvvEditText: EditText,
    private val cvvValidator: CVVValidator,
    private val inputFilterHandler: InputFilterHandler = InputFilterHandler(),
    private val validationResultHandler: ValidationResultHandler
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(cvv: Editable?) {
        val pan = panEditText.text.toString()

        val cardValidationRule = cvvValidator.getValidationRule(pan, cardConfiguration)
        inputFilterHandler.handle(
            editText = cvvEditText,
            cardValidationRule = cardValidationRule
        )

        val result = cvvValidator.validate(cvv.toString(), pan, cardConfiguration)
        validationResultHandler.handle(
            cardDetailType = CVV,
            validationResult = result.first,
            cardBrand = result.second
        )
    }

}