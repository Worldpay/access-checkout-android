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

    override fun afterTextChanged(pan: Editable?) {
        val cursorPosition = panEditText.selectionEnd
        val panText = pan.toString()
        val newCardBrand = findBrandForPan(panText)

        val formattedPan = panFormatter.format(panText, newCardBrand)

        handleCardBrandChange(newCardBrand)

        val cardValidationRule = getPanValidationRule(newCardBrand)
        val validationState = panValidator.validate(formattedPan, cardValidationRule, newCardBrand)

        val isValid = validationState == VALID
        val forceNotify = validationState == CARD_BRAND_NOT_ACCEPTED

        panValidationResultHandler.handleResult(isValid, forceNotify)
        if (formattedPan != panText) {
            updatePanText(formattedPan, panText, cursorPosition)
        }
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

    private fun updatePanText(
        formattedPan: String,
        panText: String,
        originalCursorPosition: Int
    ) {
        setText(formattedPan)
        setCursorPosition(panText, originalCursorPosition, formattedPan)
    }

    private fun setCursorPosition(
        panText: String,
        originalCursorPosition: Int,
        formattedPan: String
    ) {
        val previousSpacesToLeft = spacesToLeft(panText, originalCursorPosition - 1)
        val newSpacesToLeft = spacesToLeft(formattedPan, originalCursorPosition - 1)
        panEditText.setSelection(originalCursorPosition + newSpacesToLeft - previousSpacesToLeft)
    }

    private fun setText(text: String) {
        panEditText.removeTextChangedListener(this)
        panEditText.setText(text)
        panEditText.addTextChangedListener(this)
    }

    private fun spacesToLeft(pan: String, limit: Int): Int {
        var spacesToLeft = 0
        val substr = pan.substring(0..limit)
        for (char in substr) {
            if (Regex("\\s+").matches(char.toString())) {
                spacesToLeft += 1
            }
        }
        return spacesToLeft
    }
}
