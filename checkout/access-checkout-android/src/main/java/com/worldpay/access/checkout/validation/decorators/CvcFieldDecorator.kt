package com.worldpay.access.checkout.validation.decorators

import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.validation.filters.LengthFilterFactory
import com.worldpay.access.checkout.validation.listeners.focus.CvcFocusChangeListener

internal class CvcFieldDecorator(
    private val cvcTextWatcher : TextWatcher,
    private val cvcFocusChangeListener : CvcFocusChangeListener,
    private val lengthFilterFactory : LengthFilterFactory,
    private val cvcEditText : EditText,
    private val panEditText : EditText?
) : AbstractFieldDecorator() {

    private var addedCvcTextWatcher: TextWatcher? = null

    fun decorate() {
        addTextWatcher()

        if (cvcEditText.isCursorVisible) {
            cvcEditText.setText(cvcEditText.text.toString())
        }

        cvcEditText.onFocusChangeListener = cvcFocusChangeListener

        applyFilter(cvcEditText, lengthFilterFactory.getCvcLengthFilter(panEditText))

        cvcEditText.setHint(R.string.card_cvc_hint)
    }

    private fun addTextWatcher() {
        if (addedCvcTextWatcher != null) {
            cvcEditText.removeTextChangedListener(addedCvcTextWatcher)
        }
        addedCvcTextWatcher = cvcTextWatcher
        cvcEditText.addTextChangedListener(cvcTextWatcher)
    }

}
