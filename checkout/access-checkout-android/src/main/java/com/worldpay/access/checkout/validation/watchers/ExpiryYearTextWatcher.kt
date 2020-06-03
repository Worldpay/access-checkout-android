package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.ValidationResultHandler
import com.worldpay.access.checkout.validation.ValidationRuleHandler
import com.worldpay.access.checkout.validation.card.CardDetailType
import com.worldpay.access.checkout.validation.validators.DateValidator

internal class ExpiryYearTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private val dateValidator: DateValidator,
    private val validationRuleHandler: ValidationRuleHandler,
    private val validationResultHandler: ValidationResultHandler
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(year: Editable?) {
        val cardValidationRule = dateValidator.getValidationRule(cardConfiguration)
        validationRuleHandler.handle(
            cardDetailType = CardDetailType.EXPIRY_YEAR,
            cardValidationRule = cardValidationRule.second
        )

        val result = dateValidator.validate(null, year.toString(), cardConfiguration)
        validationResultHandler.handle(
            cardDetailType = CardDetailType.EXPIRY_YEAR,
            validationResult = result
        )
    }

}