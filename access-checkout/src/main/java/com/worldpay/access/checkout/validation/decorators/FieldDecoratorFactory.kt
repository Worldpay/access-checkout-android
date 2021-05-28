package com.worldpay.access.checkout.validation.decorators

import android.widget.EditText
import com.worldpay.access.checkout.validation.filters.LengthFilterFactory
import com.worldpay.access.checkout.validation.listeners.focus.FocusChangeListenerFactory
import com.worldpay.access.checkout.validation.listeners.text.TextWatcherFactory

internal class FieldDecoratorFactory(
    private val textWatcherFactory: TextWatcherFactory,
    private val focusChangeListenerFactory : FocusChangeListenerFactory,
    private val lengthFilterFactory : LengthFilterFactory
) {

    fun getCvcDecorator(cvcEditText : EditText, panEditText : EditText?) : CvcFieldDecorator {
        return CvcFieldDecorator(
            cvcTextWatcher = textWatcherFactory.createCvcTextWatcher(),
            cvcFocusChangeListener = focusChangeListenerFactory.createCvcFocusChangeListener(),
            cvcLengthFilter = lengthFilterFactory.getCvcLengthFilter(panEditText),
            cvcEditText = cvcEditText
        )
    }

    fun getPanDecorator(
        panEditText: EditText,
        cvcEditText: EditText,
        acceptedCardBrands: Array<String>,
        disablePanFormatting: Boolean
    ) : PanFieldDecorator {
        return PanFieldDecorator(
            panTextWatcher = textWatcherFactory.createPanTextWatcher(
                panEditText,
                cvcEditText,
                acceptedCardBrands,
                disablePanFormatting
            ),
            panFocusChangeListener = focusChangeListenerFactory.createPanFocusChangeListener(),
            panLengthFilter = lengthFilterFactory.getPanLengthFilter(disablePanFormatting),
            panEditText = panEditText
        )
    }

    fun getExpiryDateDecorator(expiryDateEditText: EditText) : ExpiryDateFieldDecorator {
        return ExpiryDateFieldDecorator(
            expiryDateTextWatcher = textWatcherFactory.createExpiryDateTextWatcher(expiryDateEditText),
            expiryDateFocusChangeListener = focusChangeListenerFactory.createExpiryDateFocusChangeListener(),
            expiryDateLengthFilter = lengthFilterFactory.getExpiryDateLengthFilter(),
            expiryDateEditText = expiryDateEditText
        )
    }

}
