package com.worldpay.access.checkout.validation.decorators

import android.widget.EditText
import com.worldpay.access.checkout.validation.filters.AccessCheckoutInputFilterFactory
import com.worldpay.access.checkout.validation.listeners.focus.FocusChangeListenerFactory
import com.worldpay.access.checkout.validation.listeners.text.TextWatcherFactory

internal class FieldDecoratorFactory(
    private val textWatcherFactory: TextWatcherFactory,
    private val focusChangeListenerFactory: FocusChangeListenerFactory,
    private val accessCheckoutInputFilterFactory: AccessCheckoutInputFilterFactory
) {

    fun getCvcDecorator(cvcEditText: EditText, panEditText: EditText?): CvcFieldDecorator {
        return CvcFieldDecorator(
            cvcTextWatcher = textWatcherFactory.createCvcTextWatcher(),
            cvcFocusChangeListener = focusChangeListenerFactory.createCvcFocusChangeListener(),
            cvcLengthFilter = accessCheckoutInputFilterFactory.getCvcLengthFilter(panEditText),
            cvcEditText = cvcEditText
        )
    }

    fun getPanDecorator(
        panEditText: EditText,
        cvcEditText: EditText,
        acceptedCardBrands: Array<String>,
        enablePanFormatting: Boolean
    ): PanFieldDecorator {
        return PanFieldDecorator(
            panTextWatcher = textWatcherFactory.createPanTextWatcher(
                panEditText,
                cvcEditText,
                acceptedCardBrands,
                enablePanFormatting
            ),
            panFocusChangeListener = focusChangeListenerFactory.createPanFocusChangeListener(),
            panNumericFilter = accessCheckoutInputFilterFactory.getPanNumericFilter(),
            panEditText = panEditText,
            panFormattingEnabled = enablePanFormatting
        )
    }

    fun getExpiryDateDecorator(expiryDateEditText: EditText): ExpiryDateFieldDecorator {
        return ExpiryDateFieldDecorator(
            expiryDateTextWatcher = textWatcherFactory.createExpiryDateTextWatcher(expiryDateEditText),
            expiryDateFocusChangeListener = focusChangeListenerFactory.createExpiryDateFocusChangeListener(),
            expiryDateLengthFilter = accessCheckoutInputFilterFactory.getExpiryDateLengthFilter(),
            expiryDateEditText = expiryDateEditText
        )
    }
}
