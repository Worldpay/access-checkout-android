package com.worldpay.access.checkout.validation.decorators

import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.validation.filters.PanLengthFilter
import com.worldpay.access.checkout.validation.listeners.focus.PanFocusChangeListener
import com.worldpay.access.checkout.validation.listeners.text.PANTextWatcher

internal class PanFieldDecorator(
    private val panTextWatcher: PANTextWatcher,
    private val panFocusChangeListener: PanFocusChangeListener,
    private val panLengthFilter: PanLengthFilter,
    private val panEditText: EditText
) : AbstractFieldDecorator() {

    private var addedPanTextWatcher: TextWatcher? = null

    fun decorate() {
        addTextWatcher()

        if (panEditText.isCursorVisible) {
            panEditText.setText(panEditText.text.toString())
        }

        panEditText.onFocusChangeListener = panFocusChangeListener

        applyFilter(panEditText, panLengthFilter)

        panEditText.setHint(R.string.card_number_hint)
    }

    private fun addTextWatcher() {
        if (addedPanTextWatcher != null) {
            panEditText.removeTextChangedListener(addedPanTextWatcher)
        }
        addedPanTextWatcher = panTextWatcher
        panEditText.addTextChangedListener(addedPanTextWatcher)
    }

}
