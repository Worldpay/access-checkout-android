package com.worldpay.access.checkout.validation.watchers

import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.ValidationResultHandler
import com.worldpay.access.checkout.validation.card.CardDetailComponents
import com.worldpay.access.checkout.validation.card.CardDetailType
import com.worldpay.access.checkout.validation.card.CardDetailType.*
import com.worldpay.access.checkout.validation.validators.CVVValidator
import com.worldpay.access.checkout.validation.validators.DateValidator
import com.worldpay.access.checkout.validation.validators.PANValidator

class TextWatcherFactory(
    private val validationResultHandler: ValidationResultHandler,
    private val cardDetailComponents: CardDetailComponents
) {

    private val panValidator = PANValidator()
    private val dateValidator = DateValidator()
    private val cvvValidator = CVVValidator()

    fun createTextWatcher(cardDetailType: CardDetailType, cardConfiguration: CardConfiguration): TextWatcher {
        if (cardDetailType == PAN) {
            return PANTextWatcher(
                cardConfiguration = cardConfiguration,
                panValidator = panValidator,
                panEditText = cardDetailComponents.pan as EditText,
                validationResultHandler = validationResultHandler
            )
        }

        if (cardDetailType == EXPIRY_MONTH) {
            return ExpiryMonthTextWatcher(
                cardConfiguration = cardConfiguration,
                dateValidator = dateValidator,
                validationResultHandler = validationResultHandler,
                expiryMonthEditText = cardDetailComponents.expiryMonth as EditText
            )
        }

        if (cardDetailType == EXPIRY_YEAR) {
            return ExpiryYearTextWatcher(
                cardConfiguration = cardConfiguration,
                dateValidator = dateValidator,
                validationResultHandler = validationResultHandler,
                expiryYearEditText = cardDetailComponents.expiryYear as EditText
            )
        }

        return CVVTextWatcher(
            cardConfiguration = cardConfiguration,
            panEditText = cardDetailComponents.pan as EditText,
            cvvEditText = cardDetailComponents.cvv as EditText,
            cvvValidator = cvvValidator,
            validationResultHandler = validationResultHandler
        )
    }

}