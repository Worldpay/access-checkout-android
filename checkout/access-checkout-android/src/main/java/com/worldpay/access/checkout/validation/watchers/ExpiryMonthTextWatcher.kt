package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.ValidationResultHandler
import com.worldpay.access.checkout.validation.ValidationRuleHandler
import com.worldpay.access.checkout.validation.card.CardDetailType.EXPIRY_MONTH
import com.worldpay.access.checkout.validation.validators.DateValidator

internal class ExpiryMonthTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private val dateValidator: DateValidator,
    private val validationRuleHandler: ValidationRuleHandler,
    private val validationResultHandler: ValidationResultHandler
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(month: Editable?) {
        val cardValidationRule = dateValidator.getValidationRule(cardConfiguration)
        validationRuleHandler.handle(
            cardDetailType = EXPIRY_MONTH,
            cardValidationRule = cardValidationRule.first
        )

        val result = dateValidator.validate(month.toString(), null, cardConfiguration)
        validationResultHandler.handle(
            cardDetailType = EXPIRY_MONTH,
            validationResult = result
        )
    }

}