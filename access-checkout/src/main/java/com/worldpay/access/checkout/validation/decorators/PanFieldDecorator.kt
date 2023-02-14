package com.worldpay.access.checkout.validation.decorators

import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.validation.configuration.CardConfigurationObserver
import com.worldpay.access.checkout.validation.filters.PanNumericFilter
import com.worldpay.access.checkout.validation.listeners.focus.PanFocusChangeListener
import com.worldpay.access.checkout.validation.listeners.text.PanTextWatcher

internal class PanFieldDecorator(
    private val panTextWatcher: PanTextWatcher,
    private val panFocusChangeListener: PanFocusChangeListener,
    private val panNumericFilter: PanNumericFilter,
    private val panEditText: EditText
) : AbstractFieldDecorator(), CardConfigurationObserver {

    internal companion object {
        private var addedPanTextWatcher: TextWatcher? = null
    }

    fun decorate() {
        addTextWatcher()

        if (panEditText.isCursorVisible) {
            panEditText.setText(panEditText.text.toString())
        }

        panEditText.onFocusChangeListener = panFocusChangeListener

        applyFilter(panEditText, panNumericFilter)
    }

    private fun addTextWatcher() {
        if (addedPanTextWatcher != null) {
            panEditText.removeTextChangedListener(addedPanTextWatcher)
        }
        addedPanTextWatcher = panTextWatcher
        panEditText.addTextChangedListener(addedPanTextWatcher)
    }

    override fun update() = decorate()
}
