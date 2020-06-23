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
            lengthFilterFactory = lengthFilterFactory,
            cvcEditText = cvcEditText,
            panEditText = panEditText
        )
    }

    fun getPanDecorator(panEditText: EditText, cvcEditText: EditText) : PanFieldDecorator {
        return PanFieldDecorator(
            textWatcherFactory = textWatcherFactory,
            panFocusChangeListener = focusChangeListenerFactory.createPanFocusChangeListener(),
            lengthFilterFactory = lengthFilterFactory,
            cvcEditText = cvcEditText,
            panEditText = panEditText
        )
    }

    fun getExpiryDateDecorator(expiryDateEditText: EditText) : ExpiryDateFieldDecorator {
        return ExpiryDateFieldDecorator(
            expiryDateTextWatcher = textWatcherFactory.createExpiryDateTextWatcher(expiryDateEditText),
            expiryDateFocusChangeListener = focusChangeListenerFactory.createExpiryDateFocusChangeListener(),
            lengthFilterFactory = lengthFilterFactory,
            expiryDateEditText = expiryDateEditText
        )
    }

}
