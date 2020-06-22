package com.worldpay.access.checkout.validation.controller

import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.filters.CvcLengthFilter
import com.worldpay.access.checkout.validation.filters.ExpiryDateLengthFilter
import com.worldpay.access.checkout.validation.filters.PanLengthFilter
import com.worldpay.access.checkout.validation.filters.VariableLengthFilter
import com.worldpay.access.checkout.validation.listeners.focus.FocusChangeListenerFactory
import com.worldpay.access.checkout.validation.listeners.text.TextWatcherFactory

internal class FieldDecoratorFactory(
    private val textWatcherFactory: TextWatcherFactory,
    private val focusChangeListenerFactory : FocusChangeListenerFactory
) {

    private var cvcTextWatcher: TextWatcher? = null
    private var panTextWatcher: TextWatcher? = null
    private var expiryDateTextWatcher: TextWatcher? = null

    fun decorateCvcField(cvcEditText: EditText, panEditText: EditText?, cardConfiguration: CardConfiguration) {
        if (cvcTextWatcher != null) {
            cvcEditText.removeTextChangedListener(cvcTextWatcher)
        }
        cvcTextWatcher = textWatcherFactory.createCvcTextWatcher()
        cvcEditText.addTextChangedListener(cvcTextWatcher)
        if (cvcEditText.isCursorVisible) {
            cvcEditText.setText(cvcEditText.text.toString())
        }

        cvcEditText.onFocusChangeListener = focusChangeListenerFactory.createCvcFocusChangeListener()

        applyFilter(cvcEditText, CvcLengthFilter(panEditText, cardConfiguration))

        cvcEditText.setHint(R.string.card_cvc_hint)
    }

    fun decoratePanField(panEditText: EditText, cvcEditText: EditText, cardConfiguration: CardConfiguration) {
        if (panTextWatcher != null) {
            panEditText.removeTextChangedListener(panTextWatcher)
        }
        panTextWatcher = textWatcherFactory.createPanTextWatcher(cvcEditText, cardConfiguration)
        panEditText.addTextChangedListener(panTextWatcher)
        if (panEditText.isCursorVisible) {
            panEditText.setText(panEditText.text.toString())
        }

        panEditText.onFocusChangeListener = focusChangeListenerFactory.createPanFocusChangeListener()

        applyFilter(panEditText, PanLengthFilter(cardConfiguration))

        panEditText.setHint(R.string.card_number_hint)
    }

    fun decorateExpiryDateFields(expiryDateEditText: EditText, cardConfiguration: CardConfiguration) {
        if (expiryDateTextWatcher != null) {
            expiryDateEditText.removeTextChangedListener(expiryDateTextWatcher)
        }
        expiryDateTextWatcher = textWatcherFactory.createExpiryDateTextWatcher(expiryDateEditText)
        expiryDateEditText.addTextChangedListener(expiryDateTextWatcher)
        if (expiryDateEditText.isCursorVisible) {
            expiryDateEditText.setText(expiryDateEditText.text.toString())
        }

        expiryDateEditText.onFocusChangeListener = focusChangeListenerFactory.createExpiryDateFocusChangeListener()

        applyFilter(expiryDateEditText, ExpiryDateLengthFilter(cardConfiguration))

        expiryDateEditText.setHint(R.string.card_expiry_date_hint)
    }

    private fun applyFilter(editText: EditText, variableLengthFilter: VariableLengthFilter) {
        val filters = mutableListOf<InputFilter>()
        for (filter in editText.filters) {
            if (filter !is VariableLengthFilter && filter !is InputFilter.LengthFilter) {
                filters.add(filter)
            }
        }

        filters.add(variableLengthFilter)
        editText.filters = filters.toTypedArray()
    }

}
