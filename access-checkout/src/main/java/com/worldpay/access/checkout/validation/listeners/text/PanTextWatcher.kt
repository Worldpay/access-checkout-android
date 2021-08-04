package com.worldpay.access.checkout.validation.listeners.text

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.validation.formatter.PanFormatter
import com.worldpay.access.checkout.validation.result.handler.BrandChangedHandler
import com.worldpay.access.checkout.validation.result.handler.PanValidationResultHandler
import com.worldpay.access.checkout.validation.utils.ValidationUtil
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

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        super.beforeTextChanged(s, start, count, after)
        panBefore = s.toString()
    }

    /**
     * @param start - the position of cursor when text changed
     * @param before - the number of characters changed
     * @param count - number of characters added
     */
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        super.onTextChanged(s, start, before, count)

        if (s.isBlank()) return

        val panText = s.toString()

        if (count == 0) {
            isSpaceDeleted = panBefore[start] == ' ' && before == 1
            expectedCursorPosition = getExpectedCursorPositionOnDelete(start)
        } else {
            isSpaceDeleted = false
            val currentCursorPosition = count + start
            expectedCursorPosition = getExpectedCursorPositionOnInsert(panText, currentCursorPosition)
        }
    }

    override fun afterTextChanged(pan: Editable) {
        var panText = pan.toString()

        if (isSpaceDeleted) {
            panText = StringBuilder(panText).deleteCharAt(expectedCursorPosition).toString()
            setText(panText, expectedCursorPosition)
        }

        val brand = findBrandForPan(panText)
        val newPan = if (panFormatter.isFormattingEnabled()) getFormattedPan(panText, brand) else panText
        val cardValidationRule = getPanValidationRule(brand)

        trimToMaxLength(cardValidationRule, newPan)

        handleCardBrandChange(brand)

        validate(newPan, cardValidationRule, brand)

        if (newPan != panText) {
            setText(newPan, expectedCursorPosition)
        }

        if (panEditText.selectionEnd != expectedCursorPosition && panEditText.length() >= expectedCursorPosition) {
            panEditText.setSelection(expectedCursorPosition)
        }
    }

    private fun getExpectedCursorPositionOnDelete(start: Int): Int {
        return if (isSpaceDeleted) {
            start - 1
        } else {
            start
        }
    }

    private fun getExpectedCursorPositionOnInsert(panText: String, currentCursorPosition: Int): Int {
        val pan = if (panFormatter.isFormattingEnabled()) getFormattedPan(panText) else panText

        if (panBefore.isBlank()) return pan.length

        // guard against outOfBounds exception if new pan has space added at the end
        val panCursorPosition = if (currentCursorPosition == panText.length) {
            pan.length
        } else {
            currentCursorPosition
        }

        val spaceDiffLeft = pan.substring(0, panCursorPosition).count { it == ' ' } - panText.substring(0, currentCursorPosition).count { it == ' ' }

        val expectedCursorPosition = when {
            spaceDiffLeft > 0 -> currentCursorPosition + spaceDiffLeft
            spaceDiffLeft < 0 -> pan.length
            else -> currentCursorPosition
        }

        // jump over the space is the expected cursor is on a space
        if (pan.length > expectedCursorPosition && pan[expectedCursorPosition] == ' ') {
            return expectedCursorPosition + 1
        }

        return expectedCursorPosition
    }

    private fun validate(
        formattedPan: String,
        cardValidationRule: CardValidationRule,
        newCardBrand: RemoteCardBrand?
    ) {
        val validationState = panValidator.validate(formattedPan, cardValidationRule, newCardBrand)
        val isValid = validationState == VALID
        val forceNotify = validationState == CARD_BRAND_NOT_ACCEPTED

        panValidationResultHandler.handleResult(isValid, forceNotify)
    }

    private fun trimToMaxLength(cardValidationRule: CardValidationRule, pan: String) {
        val maxLength = ValidationUtil.getMaxLength(cardValidationRule)
        val expectedNumberOfSpaces = panFormatter.getExpectedNumberOfSpaces(pan)
        val totalMaxLength = maxLength + expectedNumberOfSpaces

        if (pan.length > totalMaxLength) {
            val charsToDrop = pan.length - totalMaxLength
            setText(pan.dropLast(charsToDrop), expectedCursorPosition - charsToDrop)
        }
    }

    private fun getFormattedPan(
        panText: String,
        cardBrand: RemoteCardBrand? = findBrandForPan(panText)
    ) = panFormatter.format(panText, cardBrand)

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

    private fun setText(text: String, cursorPosition: Int) {
        panEditText.removeTextChangedListener(this)
        panEditText.setText(text)
        panEditText.addTextChangedListener(this)
        panEditText.setSelection(cursorPosition)
    }
}
