package com.worldpay.access.checkout.validation.controller

import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.filters.*
import com.worldpay.access.checkout.validation.watchers.TextWatcherFactory

internal class FieldDecoratorFactory(
    private val textWatcherFactory: TextWatcherFactory
) {

    private var cvvTextWatcher: TextWatcher? = null
    private var panTextWatcher: TextWatcher? = null
    private var expiryMonthTextWatcher: TextWatcher? = null
    private var expiryYearTextWatcher: TextWatcher? = null

    fun decorateCvvField(cvvEditText: EditText, panEditText: EditText?, cardConfiguration: CardConfiguration) {
        if (cvvTextWatcher != null) {
            cvvEditText.removeTextChangedListener(cvvTextWatcher)
        }
        cvvTextWatcher = textWatcherFactory.createCvvTextWatcher()
        cvvEditText.addTextChangedListener(cvvTextWatcher)

        applyFilter(cvvEditText,
            CvvLengthFilter(
                panEditText,
                cardConfiguration
            )
        )
    }

    fun decoratePanField(panEditText: EditText, cvvEditText: EditText, cardConfiguration: CardConfiguration) {
        if (panTextWatcher != null) {
            panEditText.removeTextChangedListener(panTextWatcher)
        }
        panTextWatcher = textWatcherFactory.createPanTextWatcher(cvvEditText, cardConfiguration)
        panEditText.addTextChangedListener(panTextWatcher)

        applyFilter(panEditText,
            PanLengthFilter(
                cardConfiguration
            )
        )
    }

    fun decorateExpiryDateFields(monthEditText: EditText, yearEditText: EditText, cardConfiguration: CardConfiguration) {
        decorateExpMonthField(monthEditText, yearEditText, cardConfiguration)
        decorateExpYearField(yearEditText, monthEditText, cardConfiguration)
    }

    private fun decorateExpMonthField(monthEditText: EditText, yearEditText: EditText, cardConfiguration: CardConfiguration) {
        if (expiryMonthTextWatcher != null) {
            monthEditText.removeTextChangedListener(expiryMonthTextWatcher)
        }
        expiryMonthTextWatcher = textWatcherFactory.createExpiryMonthTextWatcher(yearEditText)
        monthEditText.addTextChangedListener(expiryMonthTextWatcher)

        applyFilter(monthEditText,
            ExpiryMonthLengthFilter(
                cardConfiguration
            )
        )
    }

    private fun decorateExpYearField(yearEditText: EditText, monthEditText: EditText, cardConfiguration: CardConfiguration) {
        if (expiryYearTextWatcher != null) {
            yearEditText.removeTextChangedListener(expiryYearTextWatcher)
        }
        expiryYearTextWatcher = textWatcherFactory.createExpiryYearTextWatcher(monthEditText)
        yearEditText.addTextChangedListener(expiryYearTextWatcher)

        applyFilter(yearEditText,
            ExpiryYearLengthFilter(
                cardConfiguration
            )
        )
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
