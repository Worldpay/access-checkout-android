package com.worldpay.access.checkout.validation.decorators

import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.filters.LengthFilterFactory
import com.worldpay.access.checkout.validation.listeners.focus.ExpiryDateFocusChangeListener

internal class ExpiryDateFieldDecorator(
    private val expiryDateTextWatcher : TextWatcher,
    private val expiryDateFocusChangeListener : ExpiryDateFocusChangeListener,
    private val lengthFilterFactory : LengthFilterFactory,
    private val expiryDateEditText : EditText
) : AbstractFieldDecorator() {

    private var addedExpiryDateTextWatcher: TextWatcher? = null

    fun decorate(cardConfiguration: CardConfiguration) {
        addTextWatcher()

        if (expiryDateEditText.isCursorVisible) {
            expiryDateEditText.setText(expiryDateEditText.text.toString())
        }

        expiryDateEditText.onFocusChangeListener = expiryDateFocusChangeListener

        applyFilter(expiryDateEditText, lengthFilterFactory.getExpiryDateLengthFilter(cardConfiguration))

        expiryDateEditText.setHint(R.string.card_expiry_date_hint)
    }

    private fun addTextWatcher() {
        if (addedExpiryDateTextWatcher != null) {
            expiryDateEditText.removeTextChangedListener(addedExpiryDateTextWatcher)
        }
        addedExpiryDateTextWatcher = expiryDateTextWatcher
        expiryDateEditText.addTextChangedListener(expiryDateTextWatcher)
    }

}
