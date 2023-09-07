package com.worldpay.access.checkout.validation.decorators

import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.ui.AccessEditText
import com.worldpay.access.checkout.validation.configuration.CardConfigurationObserver
import com.worldpay.access.checkout.validation.filters.ExpiryDateLengthFilter
import com.worldpay.access.checkout.validation.listeners.focus.ExpiryDateFocusChangeListener
import com.worldpay.access.checkout.validation.listeners.text.ExpiryDateTextWatcher

internal class ExpiryDateFieldDecorator(
    private val expiryDateTextWatcher: ExpiryDateTextWatcher,
    private val expiryDateFocusChangeListener: ExpiryDateFocusChangeListener,
    private val expiryDateLengthFilter: ExpiryDateLengthFilter,
    private val expiryDateAccessEditText: AccessEditText
) : AbstractFieldDecorator(), CardConfigurationObserver {

    companion object {
        private var addedExpiryDateTextWatcher: TextWatcher? = null
    }

    fun decorate() {
        addTextWatcher()

        if (expiryDateAccessEditText.isCursorVisible) {
            expiryDateAccessEditText.setText(expiryDateAccessEditText.text.toString())
        }

        expiryDateAccessEditText.onFocusChangeListener = expiryDateFocusChangeListener

        applyFilter(expiryDateAccessEditText, expiryDateLengthFilter)

        expiryDateAccessEditText.inputType = InputType.TYPE_CLASS_NUMBER
    }

    private fun addTextWatcher() {
        if (addedExpiryDateTextWatcher != null) {
            expiryDateAccessEditText.removeTextChangedListener(addedExpiryDateTextWatcher)
        }
        addedExpiryDateTextWatcher = expiryDateTextWatcher
        expiryDateAccessEditText.addTextChangedListener(expiryDateTextWatcher)
    }

    override fun update() = decorate()
}
