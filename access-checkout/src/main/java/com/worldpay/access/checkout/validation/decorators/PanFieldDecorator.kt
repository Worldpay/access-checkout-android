package com.worldpay.access.checkout.validation.decorators

import android.text.InputType
import android.text.TextWatcher
import com.worldpay.access.checkout.ui.AccessEditText
import com.worldpay.access.checkout.validation.configuration.CardConfigurationObserver
import com.worldpay.access.checkout.validation.filters.PanNumericFilter
import com.worldpay.access.checkout.validation.listeners.focus.PanFocusChangeListener
import com.worldpay.access.checkout.validation.listeners.text.PanTextWatcher

internal class PanFieldDecorator(
    private val panTextWatcher: PanTextWatcher,
    private val panFocusChangeListener: PanFocusChangeListener,
    private val panNumericFilter: PanNumericFilter,
    private val panAccessEditText: AccessEditText,
    private val panFormattingEnabled: Boolean
) : AbstractFieldDecorator(), CardConfigurationObserver {

    internal companion object {
        private var addedPanTextWatcher: TextWatcher? = null
    }

    fun decorate() {
        addTextWatcher()

        if (panAccessEditText.isCursorVisible) {
            panAccessEditText.setText(panAccessEditText.text.toString())
        }

        panAccessEditText.onFocusChangeListener = panFocusChangeListener

        applyFilter(panAccessEditText, panNumericFilter)

        if (panFormattingEnabled) {
            panAccessEditText.inputType = InputType.TYPE_CLASS_DATETIME
        } else {
            panAccessEditText.inputType = InputType.TYPE_CLASS_NUMBER
        }
    }

    private fun addTextWatcher() {
        if (addedPanTextWatcher != null) {
            panAccessEditText.removeTextChangedListener(addedPanTextWatcher)
        }
        addedPanTextWatcher = panTextWatcher
        panAccessEditText.addTextChangedListener(addedPanTextWatcher)
    }

    override fun update() = decorate()
}
