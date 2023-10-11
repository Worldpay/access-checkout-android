package com.worldpay.access.checkout.validation.decorators

import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.validation.configuration.CardConfigurationObserver
import com.worldpay.access.checkout.validation.filters.CvcLengthFilter
import com.worldpay.access.checkout.validation.listeners.focus.CvcFocusChangeListener
import com.worldpay.access.checkout.validation.listeners.text.CvcTextWatcher

internal class CvcFieldDecorator(
    private val cvcTextWatcher: CvcTextWatcher,
    private val cvcFocusChangeListener: CvcFocusChangeListener,
    private val cvcLengthFilter: CvcLengthFilter,
    private val cvcEditText: EditText
) : AbstractFieldDecorator(), CardConfigurationObserver {

    companion object {
        private var addedCvcTextWatcher: TextWatcher? = null
    }

    fun decorate() {
        addTextWatcher()

        if (cvcEditText.isCursorVisible) {
            cvcEditText.setText(cvcEditText.text.toString())
        }

        cvcEditText.onFocusChangeListener = cvcFocusChangeListener

        applyFilter(cvcEditText, cvcLengthFilter)

        cvcEditText.inputType = InputType.TYPE_CLASS_NUMBER
    }

    private fun addTextWatcher() {
        if (addedCvcTextWatcher != null) {
            cvcEditText.removeTextChangedListener(addedCvcTextWatcher)
        }
        addedCvcTextWatcher = cvcTextWatcher
        cvcEditText.addTextChangedListener(cvcTextWatcher)
    }

    override fun update() = decorate()
}
