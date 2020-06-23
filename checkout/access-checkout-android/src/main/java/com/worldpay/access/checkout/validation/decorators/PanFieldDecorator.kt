package com.worldpay.access.checkout.validation.decorators

import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.validation.filters.LengthFilterFactory
import com.worldpay.access.checkout.validation.listeners.focus.PanFocusChangeListener
import com.worldpay.access.checkout.validation.listeners.text.TextWatcherFactory

internal class PanFieldDecorator(
    private val textWatcherFactory : TextWatcherFactory,
    private val panFocusChangeListener : PanFocusChangeListener,
    private val lengthFilterFactory : LengthFilterFactory,
    private val panEditText : EditText,
    private val cvcEditText : EditText
) : AbstractFieldDecorator() {

    private var addedPanTextWatcher: TextWatcher? = null

    fun decorate() {
        addTextWatcher()

        if (panEditText.isCursorVisible) {
            panEditText.setText(panEditText.text.toString())
        }

        panEditText.onFocusChangeListener = panFocusChangeListener

        applyFilter(panEditText, lengthFilterFactory.getPanLengthFilter())

        panEditText.setHint(R.string.card_number_hint)
    }

    private fun addTextWatcher() {
        if (addedPanTextWatcher != null) {
            panEditText.removeTextChangedListener(addedPanTextWatcher)
        }
        addedPanTextWatcher = textWatcherFactory.createPanTextWatcher(cvcEditText)
        panEditText.addTextChangedListener(addedPanTextWatcher)
    }

}
