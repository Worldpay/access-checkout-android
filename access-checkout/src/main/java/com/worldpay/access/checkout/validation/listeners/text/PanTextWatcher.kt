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

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        super.beforeTextChanged(s, start, count, after)
        panBefore = s.toString()
    }

    override fun afterTextChanged(pan: Editable?) {
        var panText = pan.toString()

        if (isSpaceDeleted(panText)) {
            val originalCursorPosition = panEditText.selectionEnd
            panText = StringBuilder(panText).deleteCharAt(originalCursorPosition - 1).toString()
            setText(panText, originalCursorPosition - 1)
        } else if (panText.endsWith(" ")) {
            setText(panText.dropLast(1))
        }

        val newCardBrand = findBrandForPan(panText)

        val formattedPan = panFormatter.format(panText, newCardBrand)

        handleCardBrandChange(newCardBrand)

        val cardValidationRule = getPanValidationRule(newCardBrand)
        val validationState = panValidator.validate(formattedPan, cardValidationRule, newCardBrand)

        val isValid = validationState == VALID
        val forceNotify = validationState == CARD_BRAND_NOT_ACCEPTED

        panValidationResultHandler.handleResult(isValid, forceNotify)
        if (formattedPan != panText) {
            setText(formattedPan, getChangeIndex(formattedPan))
        }
    }

    private fun isSpaceDeleted(panAfter: String): Boolean {
        val after = panAfter.toCharArray()
        val before = panBefore.toCharArray()

        if (panBefore.endsWith(" ") && !panAfter.endsWith(" ")) {
            return true
        }

        if (after.size >= before.size || panBefore == "") {
            return false
        }

        // deleting characters
        if (before.size > after.size) {
            before.forEachIndexed { i, char ->
                if (after.size > i && char != after[i]) {
                    return char == ' '
                }
            }
        }

        return false
    }

    private fun getChangeIndex(panAfter: String): Int {
        val after = panAfter.toCharArray()
        val before = panBefore.toCharArray()

        // entering digits
        // loop through the text char array that we have now
        // IF
        // the size of the old text is larger than the current index and the character is not equal to
        // the character at the old text index
        // THEN
        // grab the next index and check if this is a space character, if so return that index
        // otherwise return the current index
        //
        // we return the next index so we can move the cursor after the space character
        // when entering the 5 in 1234 5
        if (after.size > before.size) {
            after.forEachIndexed { i, char ->
                if (before.size > i && char != before[i]) {
                    val nextIndex = i + 1
                    if (after.size > nextIndex && after[nextIndex] == ' ') {
                        return nextIndex + 1
                    }
                    return i
                }
            }
        }

        // deleting digits
        // loop through the text char array that we had before
        // IF
        // the size of the new text is larger than the current index and the character is not equal to
        // the character at the new text index
        // THEN
        // grab the previous index and check if this is a space character, if so return that index
        // otherwise return the current index
        //
        // we return the previous index so we can move the cursor before the space character
        // when deleting the 5 in 1234 5
        if (before.size > after.size) {
            before.forEachIndexed { i, char ->
                if (after.size > i && char != after[i]) {
                    val previousIndex = i - 1
                    if (previousIndex > 0 && after[previousIndex] == ' ') {
                        return previousIndex
                    }
                    return i
                }
            }
        }

        return after.size
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
        panEditText.setText(text)
        panEditText.addTextChangedListener(this)

        panEditText.setSelection(cursorPosition)
    }
}
