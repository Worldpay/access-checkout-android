package com.worldpay.access.checkout.validation.decorators

import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.api.configuration.CardConfiguration
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

    fun decorate(cardConfiguration: CardConfiguration) {
        addTextWatcher(cardConfiguration)

        if (panEditText.isCursorVisible) {
            panEditText.setText(panEditText.text.toString())
        }

        panEditText.onFocusChangeListener = panFocusChangeListener

        applyFilter(panEditText, lengthFilterFactory.getPanLengthFilter(cardConfiguration))

        panEditText.setHint(R.string.card_number_hint)
    }

    private fun addTextWatcher(cardConfiguration : CardConfiguration) {
        if (addedPanTextWatcher != null) {
            panEditText.removeTextChangedListener(addedPanTextWatcher)
        }
        addedPanTextWatcher = textWatcherFactory.createPanTextWatcher(cvcEditText, cardConfiguration)
        panEditText.addTextChangedListener(addedPanTextWatcher)
    }

}
