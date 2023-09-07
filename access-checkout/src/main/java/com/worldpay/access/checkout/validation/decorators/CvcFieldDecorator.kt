package com.worldpay.access.checkout.validation.decorators

import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.ui.AccessEditText
import com.worldpay.access.checkout.validation.configuration.CardConfigurationObserver
import com.worldpay.access.checkout.validation.filters.CvcLengthFilter
import com.worldpay.access.checkout.validation.listeners.focus.CvcFocusChangeListener
import com.worldpay.access.checkout.validation.listeners.text.CvcTextWatcher

internal class CvcFieldDecorator(
    private val cvcTextWatcher: CvcTextWatcher,
    private val cvcFocusChangeListener: CvcFocusChangeListener,
    private val cvcLengthFilter: CvcLengthFilter,
    private val cvcAccessEditText: AccessEditText
) : AbstractFieldDecorator(), CardConfigurationObserver {

    companion object {
        private var addedCvcTextWatcher: TextWatcher? = null
    }

    fun decorate() {
        addTextWatcher()

        if (cvcAccessEditText.isCursorVisible) {
            cvcAccessEditText.setText(cvcAccessEditText.text.toString())
        }

        cvcAccessEditText.onFocusChangeListener = cvcFocusChangeListener

        applyFilter(cvcAccessEditText, cvcLengthFilter)

        cvcAccessEditText.inputType = InputType.TYPE_CLASS_NUMBER
    }

    private fun addTextWatcher() {
        if (addedCvcTextWatcher != null) {
            cvcAccessEditText.removeTextChangedListener(addedCvcTextWatcher)
        }
        addedCvcTextWatcher = cvcTextWatcher
        cvcAccessEditText.addTextChangedListener(cvcTextWatcher)
    }

    override fun update() = decorate()
}
