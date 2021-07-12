package com.worldpay.access.checkout.validation.listeners.text

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.validation.formatter.PanFormatter
import com.worldpay.access.checkout.validation.result.handler.BrandChangedHandler
import com.worldpay.access.checkout.validation.result.handler.PanValidationResultHandler
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getCvcValidationRule
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getPanValidationRule
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CvcValidator
import com.worldpay.access.checkout.validation.validators.PanValidator
import com.worldpay.access.checkout.validation.validators.PanValidator.PanValidationResult.CARD_BRAND_NOT_ACCEPTED
import com.worldpay.access.checkout.validation.validators.PanValidator.PanValidationResult.VALID

internal class PanTextWatcher(
    private val panEditText: EditText,
    private var panValidator: PanValidator,
    private val panFormatter: PanFormatter,
    private val cvcValidator: CvcValidator,
    private val cvcEditText: EditText,
    private val panValidationResultHandler: PanValidationResultHandler,
    private val brandChangedHandler: BrandChangedHandler,
    private val cvcValidationRuleManager: CVCValidationRuleManager
) : AbstractCardDetailTextWatcher() {

    private var cardBrand: RemoteCardBrand? = null
    private var panBefore = ""

    private var expectedCursorPosition = 0
    private var isSpaceDeleted = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        super.beforeTextChanged(s, start, count, after)
        panBefore = s.toString()
    }

    /**
     * @param start - the position of cursor when text changed
     * @param before - the number of characters changed
     * @param count - number of characters added
     */
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        super.onTextChanged(s, start, before, count)

        if (s.isNullOrEmpty()) return
        if (!panFormatter.isFormattingEnabled()) return

        val panText = s.toString()

        if (count == 0) {
            isSpaceDeleted = panBefore[start] == ' ' && before == 1
            expectedCursorPosition = if (isSpaceDeleted) {
                start - 1
            } else {
                start
            }
        } else {
            val currentCursorPosition = count + start

            val formattedPan = getFormattedPan(panText)

            val spaceDiffLeft = formattedPan.substring(0, currentCursorPosition).count { it == ' ' } - panText.substring(0, currentCursorPosition).count { it == ' ' }

            expectedCursorPosition = when {
                spaceDiffLeft > 0 -> currentCursorPosition + spaceDiffLeft
                else -> currentCursorPosition
            }

            if (expectedCursorPosition > formattedPan.length) {
                expectedCursorPosition = formattedPan.length
            }

            if (formattedPan.length > expectedCursorPosition && formattedPan[expectedCursorPosition] == ' ') {
                expectedCursorPosition += 1
            }

            isSpaceDeleted = false
        }
    }

    override fun afterTextChanged(pan: Editable?) {
        if (pan.toString() == "") {
            return
        }
        var panText = pan.toString()

        if (isSpaceDeleted) {
            panText = StringBuilder(panText).deleteCharAt(expectedCursorPosition).toString()
            setText(panText, expectedCursorPosition)
        } else if (panText.endsWith(" ")) {
            setText(panText.dropLast(1))
        }

        val newCardBrand = findBrandForPan(panText)
        val formattedPan = getFormattedPan(panText, newCardBrand)

        handleCardBrandChange(newCardBrand)

        val cardValidationRule = getPanValidationRule(newCardBrand)
        val validationState = panValidator.validate(formattedPan, cardValidationRule, newCardBrand)

        val isValid = validationState == VALID
        val forceNotify = validationState == CARD_BRAND_NOT_ACCEPTED

        panValidationResultHandler.handleResult(isValid, forceNotify)
        if (formattedPan != panText) {
            setText(formattedPan, expectedCursorPosition)
        }
    }

    private fun getFormattedPan(
        panText: String,
        cardBrand: RemoteCardBrand? = findBrandForPan(panText)
    ): String {
        return panFormatter.format(panText, cardBrand)
    }

    private fun handleCardBrandChange(newCardBrand: RemoteCardBrand?) {
        if (cardBrand != newCardBrand) {
            cardBrand = newCardBrand

            brandChangedHandler.handle(newCardBrand)

            updateCvcValidationRule()

            val cvcText = cvcEditText.text.toString()
            if (cvcText.isNotBlank()) {
                cvcValidator.validate(cvcText)
            }
        }
    }

    private fun updateCvcValidationRule() {
        val cardValidationRule = getCvcValidationRule(cardBrand)
        cvcValidationRuleManager.updateRule(cardValidationRule)
    }

    private fun setText(text: String, cursorPosition: Int = text.length) {
        panEditText.removeTextChangedListener(this)
        val inputFilters = panEditText.filters
        panEditText.filters = emptyArray()
        panEditText.setText(text)
        panEditText.addTextChangedListener(this)
        panEditText.filters = inputFilters
        panEditText.setSelection(cursorPosition)
    }
}
