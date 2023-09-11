package com.worldpay.access.checkout.validation.decorators

import com.worldpay.access.checkout.ui.AccessEditText
import com.worldpay.access.checkout.validation.filters.AccessCheckoutInputFilterFactory
import com.worldpay.access.checkout.validation.listeners.focus.FocusChangeListenerFactory
import com.worldpay.access.checkout.validation.listeners.text.TextWatcherFactory

internal class FieldDecoratorFactory(
    private val textWatcherFactory: TextWatcherFactory,
    private val focusChangeListenerFactory: FocusChangeListenerFactory,
    private val accessCheckoutInputFilterFactory: AccessCheckoutInputFilterFactory
) {

    fun getCvcDecorator(cvcAccessEditText: AccessEditText, panAccessEditText: AccessEditText?): CvcFieldDecorator {
        return CvcFieldDecorator(
            cvcTextWatcher = textWatcherFactory.createCvcTextWatcher(),
            cvcFocusChangeListener = focusChangeListenerFactory.createCvcFocusChangeListener(),
            cvcLengthFilter = accessCheckoutInputFilterFactory.getCvcLengthFilter(panAccessEditText),
            cvcAccessEditText = cvcAccessEditText
        )
    }

    fun getPanDecorator(
        panAccessEditText: AccessEditText,
        cvcAccessEditText: AccessEditText,
        acceptedCardBrands: Array<String>,
        enablePanFormatting: Boolean
    ): PanFieldDecorator {
        return PanFieldDecorator(
            panTextWatcher = textWatcherFactory.createPanTextWatcher(
                panAccessEditText,
                cvcAccessEditText,
                acceptedCardBrands,
                enablePanFormatting
            ),
            panFocusChangeListener = focusChangeListenerFactory.createPanFocusChangeListener(),
            panNumericFilter = accessCheckoutInputFilterFactory.getPanNumericFilter(),
            panAccessEditText = panAccessEditText,
            panFormattingEnabled = enablePanFormatting
        )
    }

    fun getExpiryDateDecorator(expiryDateAccessEditText: AccessEditText): ExpiryDateFieldDecorator {
        return ExpiryDateFieldDecorator(
            expiryDateTextWatcher = textWatcherFactory.createExpiryDateTextWatcher(expiryDateAccessEditText),
            expiryDateFocusChangeListener = focusChangeListenerFactory.createExpiryDateFocusChangeListener(),
            expiryDateLengthFilter = accessCheckoutInputFilterFactory.getExpiryDateLengthFilter(),
            expiryDateAccessEditText = expiryDateAccessEditText
        )
    }
}
