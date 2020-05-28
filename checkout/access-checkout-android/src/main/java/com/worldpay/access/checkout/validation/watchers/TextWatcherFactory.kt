package com.worldpay.access.checkout.validation.watchers

import android.text.TextWatcher
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.client.AccessCheckoutValidationListener
import com.worldpay.access.checkout.validation.ValidationResultHandler
import com.worldpay.access.checkout.validation.ValidationRuleHandler
import com.worldpay.access.checkout.validation.card.CardDetailComponents
import com.worldpay.access.checkout.validation.card.CardDetailType
import com.worldpay.access.checkout.validation.card.CardDetailType.*
import com.worldpay.access.checkout.validation.validators.CVVValidator
import com.worldpay.access.checkout.validation.validators.DateValidator
import com.worldpay.access.checkout.validation.validators.PANValidator

class TextWatcherFactory(
    validationListener: AccessCheckoutValidationListener,
    private val cardDetailComponents: CardDetailComponents,
    private val cardConfiguration: CardConfiguration
) {

    private val validationRuleHandler = ValidationRuleHandler(cardDetailComponents)
    private val validationResultHandler = ValidationResultHandler(validationListener, cardDetailComponents)

    private val panValidator = PANValidator()
    private val dateValidator = DateValidator()
    private val cvvValidator = CVVValidator()

    fun createTextWatcher(cardDetailType: CardDetailType): TextWatcher {
        if (cardDetailType == PAN) {
            return PANTextWatcher(
                cardConfiguration = cardConfiguration,
                panValidator = panValidator,
                validationRuleHandler = validationRuleHandler,
                validationResultHandler = validationResultHandler
            )
        }

        if (cardDetailType == EXPIRY_MONTH) {
            return ExpiryMonthTextWatcher(
                cardConfiguration = cardConfiguration,
                dateValidator = dateValidator,
                validationRuleHandler = validationRuleHandler,
                validationResultHandler = validationResultHandler
            )
        }

        if (cardDetailType == EXPIRY_YEAR) {
            return ExpiryYearTextWatcher(
                cardConfiguration = cardConfiguration,
                dateValidator = dateValidator,
                validationRuleHandler = validationRuleHandler,
                validationResultHandler = validationResultHandler
            )
        }

        return CVVTextWatcher(
            cardConfiguration = cardConfiguration,
            cardDetailComponents = cardDetailComponents,
            cvvValidator = cvvValidator,
            validationRuleHandler = validationRuleHandler,
            validationResultHandler = validationResultHandler
        )
    }

}