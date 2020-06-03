package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.ValidationResultHandler
import com.worldpay.access.checkout.validation.ValidationRuleHandler
import com.worldpay.access.checkout.validation.card.CardDetailType.PAN
import com.worldpay.access.checkout.validation.validators.PANValidator

internal class PANTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private var panValidator: PANValidator,
    private val validationRuleHandler: ValidationRuleHandler,
    private val validationResultHandler: ValidationResultHandler
) : AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(pan: Editable?) {
        val cardValidationRule = panValidator.getValidationRule(pan.toString(), cardConfiguration)
        validationRuleHandler.handle(
            cardDetailType = PAN,
            cardValidationRule = cardValidationRule
        )

        val result = panValidator.validate(pan.toString(), cardConfiguration)
        validationResultHandler.handle(
            cardDetailType = PAN,
            validationResult = result.first,
            cardBrand = result.second
        )
    }

}