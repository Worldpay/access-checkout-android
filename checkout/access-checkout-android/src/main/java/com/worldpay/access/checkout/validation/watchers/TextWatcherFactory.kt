package com.worldpay.access.checkout.validation.watchers

import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.ValidationResultHandler
import com.worldpay.access.checkout.validation.validators.CVVValidator
import com.worldpay.access.checkout.validation.validators.DateValidator
import com.worldpay.access.checkout.validation.validators.PANValidator

class TextWatcherFactory(
    private val validationResultHandler: ValidationResultHandler
) {

    private val panValidator = PANValidator()
    private val dateValidator = DateValidator()
    private val cvvValidator = CVVValidator()

    fun createPanTextWatcher(panEditText: EditText, cardConfiguration: CardConfiguration): TextWatcher {
        return PANTextWatcher(
            cardConfiguration = cardConfiguration,
            panValidator = panValidator,
            panEditText = panEditText,
            validationResultHandler = validationResultHandler
        )
    }

    fun createExpiryMonthTextWatcher(expiryMonthEditText: EditText, cardConfiguration: CardConfiguration): TextWatcher {
        return ExpiryMonthTextWatcher(
            cardConfiguration = cardConfiguration,
            dateValidator = dateValidator,
            validationResultHandler = validationResultHandler,
            expiryMonthEditText = expiryMonthEditText
        )
    }

    fun createExpiryYearTextWatcher(expiryYearEditText: EditText, cardConfiguration: CardConfiguration): TextWatcher {
        return ExpiryYearTextWatcher(
            cardConfiguration = cardConfiguration,
            dateValidator = dateValidator,
            validationResultHandler = validationResultHandler,
            expiryYearEditText = expiryYearEditText
        )
    }

    fun createCvvTextWatcher(cvvEditText: EditText, panEditText: EditText?, cardConfiguration: CardConfiguration): TextWatcher {
        return CVVTextWatcher(
            cardConfiguration = cardConfiguration,
            panEditText = panEditText,
            cvvEditText = cvvEditText,
            cvvValidator = cvvValidator,
            validationResultHandler = validationResultHandler
        )
    }

}