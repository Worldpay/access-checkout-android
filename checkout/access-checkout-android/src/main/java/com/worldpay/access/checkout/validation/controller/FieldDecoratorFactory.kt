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
        cvvTextWatcher = textWatcherFactory.createCvvTextWatcher(cvvEditText, cardConfiguration)
        cvvEditText.addTextChangedListener(cvvTextWatcher)

        applyFilter(cvvEditText,
            CvvLengthFilter(
                panEditText,
                cardConfiguration
            )
        )
    }

    fun decoratePanField(panEditText: EditText, cardConfiguration: CardConfiguration) {
        if (panTextWatcher != null) {
            panEditText.removeTextChangedListener(panTextWatcher)
        }
        panTextWatcher = textWatcherFactory.createPanTextWatcher(cardConfiguration)
        panEditText.addTextChangedListener(panTextWatcher)

        applyFilter(panEditText,
            PanLengthFilter(
                cardConfiguration
            )
        )
    }

    fun decorateExpMonthField(expiryMonthEditText: EditText, cardConfiguration: CardConfiguration) {
        if (expiryMonthTextWatcher != null) {
            expiryMonthEditText.removeTextChangedListener(expiryMonthTextWatcher)
        }
        expiryMonthTextWatcher = textWatcherFactory.createExpiryMonthTextWatcher(cardConfiguration)
        expiryMonthEditText.addTextChangedListener(expiryMonthTextWatcher)

        applyFilter(expiryMonthEditText,
            ExpiryMonthLengthFilter(
                cardConfiguration
            )
        )
    }

    fun decorateExpYearField(expiryYearEditText: EditText, cardConfiguration: CardConfiguration) {
        if (expiryYearTextWatcher != null) {
            expiryYearEditText.removeTextChangedListener(expiryYearTextWatcher)
        }
        expiryYearTextWatcher = textWatcherFactory.createExpiryYearTextWatcher(cardConfiguration)
        expiryYearEditText.addTextChangedListener(expiryYearTextWatcher)

        applyFilter(expiryYearEditText,
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
