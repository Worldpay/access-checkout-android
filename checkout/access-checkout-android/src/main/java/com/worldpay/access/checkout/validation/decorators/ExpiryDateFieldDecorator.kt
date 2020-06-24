package com.worldpay.access.checkout.validation.decorators

import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.validation.configuration.CardConfigurationObserver
import com.worldpay.access.checkout.validation.filters.ExpiryDateLengthFilter
import com.worldpay.access.checkout.validation.listeners.focus.ExpiryDateFocusChangeListener

internal class ExpiryDateFieldDecorator(
    private val expiryDateTextWatcher: TextWatcher,
    private val expiryDateFocusChangeListener: ExpiryDateFocusChangeListener,
    private val expiryDateLengthFilter: ExpiryDateLengthFilter,
    private val expiryDateEditText: EditText
) : AbstractFieldDecorator(), CardConfigurationObserver {

    private var addedExpiryDateTextWatcher: TextWatcher? = null

    fun decorate() {
        addTextWatcher()

        if (expiryDateEditText.isCursorVisible) {
            expiryDateEditText.setText(expiryDateEditText.text.toString())
        }

        expiryDateEditText.onFocusChangeListener = expiryDateFocusChangeListener

        applyFilter(expiryDateEditText, expiryDateLengthFilter)

        expiryDateEditText.setHint(R.string.card_expiry_date_hint)
    }

    private fun addTextWatcher() {
        if (addedExpiryDateTextWatcher != null) {
            expiryDateEditText.removeTextChangedListener(addedExpiryDateTextWatcher)
        }
        addedExpiryDateTextWatcher = expiryDateTextWatcher
        expiryDateEditText.addTextChangedListener(expiryDateTextWatcher)
    }

    override fun update() = decorate()

}
