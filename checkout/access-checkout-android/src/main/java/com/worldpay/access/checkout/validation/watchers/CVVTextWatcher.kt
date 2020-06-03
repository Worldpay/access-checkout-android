package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.ValidationResultHandler
import com.worldpay.access.checkout.validation.ValidationRuleHandler
import com.worldpay.access.checkout.validation.card.CardDetailComponents
import com.worldpay.access.checkout.validation.card.CardDetailType.CVV
import com.worldpay.access.checkout.validation.validators.CVVValidator

internal class CVVTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private val cardDetailComponents: CardDetailComponents,
    private val cvvValidator: CVVValidator,
    private val validationRuleHandler: ValidationRuleHandler,
    private val validationResultHandler: ValidationResultHandler
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(cvv: Editable?) {
        val pan = cardDetailComponents.pan.text.toString()

        val cardValidationRule = cvvValidator.getValidationRule(pan, cardConfiguration)
        validationRuleHandler.handle(
            cardDetailType = CVV,
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