package com.worldpay.access.checkout.validation.controller

import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.filters.CvvLengthFilter
import com.worldpay.access.checkout.validation.filters.ExpiryDateLengthFilter
import com.worldpay.access.checkout.validation.filters.PanLengthFilter
import com.worldpay.access.checkout.validation.filters.VariableLengthFilter
import com.worldpay.access.checkout.validation.watchers.TextWatcherFactory

internal class FieldDecoratorFactory(
    private val textWatcherFactory: TextWatcherFactory
) {

    private var cvvTextWatcher: TextWatcher? = null
    private var panTextWatcher: TextWatcher? = null
    private var expiryDateTextWatcher: TextWatcher? = null

    fun decorateCvvField(cvvEditText: EditText, panEditText: EditText?, cardConfiguration: CardConfiguration) {
        if (cvvTextWatcher != null) {
            cvvEditText.removeTextChangedListener(cvvTextWatcher)
        }
        cvvTextWatcher = textWatcherFactory.createCvvTextWatcher()
        cvvEditText.addTextChangedListener(cvvTextWatcher)
        if (cvvEditText.isCursorVisible) {
            cvvEditText.setText(cvvEditText.text.toString())
        }

        applyFilter(cvvEditText, CvvLengthFilter(panEditText, cardConfiguration))

        cvvEditText.setHint(R.string.card_cvv_hint)
    }

    fun decoratePanField(panEditText: EditText, cvvEditText: EditText, cardConfiguration: CardConfiguration) {
        if (panTextWatcher != null) {
            panEditText.removeTextChangedListener(panTextWatcher)
        }
        panTextWatcher = textWatcherFactory.createPanTextWatcher(cvvEditText, cardConfiguration)
        panEditText.addTextChangedListener(panTextWatcher)
        if (panEditText.isCursorVisible) {
            panEditText.setText(panEditText.text.toString())
        }

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
