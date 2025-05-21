package com.worldpay.access.checkout.validation.listeners.text

import android.text.Editable
import android.util.Log
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.service.BrandService
import com.worldpay.access.checkout.validation.formatter.PanFormatter
import com.worldpay.access.checkout.validation.result.handler.BrandsChangedHandler
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
import java.lang.Math.min

internal class PanTextWatcher(
    private val panEditText: EditText,
    private var panValidator: PanValidator,
    private val panFormatter: PanFormatter,
    private val cvcValidator: CvcValidator,
    private val cvcAccessEditText: EditText,
    private val panValidationResultHandler: PanValidationResultHandler,
    private val brandsChangedHandler: BrandsChangedHandler,
    private val cvcValidationRuleManager: CVCValidationRuleManager,
    private val brandService: BrandService
) : AbstractCardDetailTextWatcher() {

    private var cardBrands: List<RemoteCardBrand?> = emptyList()
    private var panBefore = ""
    private var cursorPositionBefore = 0

    private var expectedCursorPosition = 0
    private var isSpaceDeleted = false

    private val requiredPanLengthForCardBrands = 12

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        super.beforeTextChanged(s, start, count, after)
        panBefore = s.toString()
        cursorPositionBefore = start
    }

    /**
     * This function is called whenever the text is being changed in the UI.
     * The override is responsible for calculating where the cursor position should be after the text changes
     *
     * @param start the position of cursor when text changed
     * @param before the number of characters changed
     * @param count number of characters added
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
            expectedCursorPosition =
                getExpectedCursorPositionOnInsert(panText, currentCursorPosition)
        }
    }

    override fun afterTextChanged(pan: Editable) {
        var panText = pan.toString()

        if (isSpaceDeleted) {
            panText = StringBuilder(panText).deleteCharAt(expectedCursorPosition).toString()
            setText(panText, expectedCursorPosition)
            isSpaceDeleted = false
        }

        val brand = findBrandForPan(panText)
        val newPan =
            if (panFormatter.isFormattingEnabled()) getFormattedPan(panText, brand) else panText
        val cardValidationRule = getPanValidationRule(brand)

        if (trimToMaxLength(cardValidationRule, newPan)) {
            clearPanBefore()
            return
        }

        val brands = if (isPanRequiredLength())  brandService.getCardBrands(brand, newPan) else listOf(brand)

        handleCardBrandChange(brands)

        validate(newPan, cardValidationRule, brand)

        if (newPan != panText) {
            if (newPan == panBefore) {
                expectedCursorPosition = cursorPositionBefore
            }
            setText(newPan, expectedCursorPosition)
        }

        if (panEditText.selectionEnd != expectedCursorPosition && panEditText.length() >= expectedCursorPosition) {
            panEditText.setSelection(expectedCursorPosition)
        }

        clearPanBefore()
    }

    /**
     * Designed to clear from memory the text held in the panBefore property
     */
    private fun clearPanBefore() {
        this.panBefore = ""
    }

    /**
     * Calculates where the expected cursor should be when character(s) has been deleted
     *
     * @return Int - the expected cursor position
     */
    private fun getExpectedCursorPositionOnDelete(start: Int): Int {
        return if (isSpaceDeleted) {
            start - 1
        } else {
            start
        }
    }

    /**
     * Calculates where the expected cursor should be when character(s) has been added
     * This will shift the cursor position by taking into account the spaces in the pan (when formatting is enabled)
     *
     * @return Int - the expected cursor position
     */
    private fun getExpectedCursorPositionOnInsert(
        panText: String,
        currentCursorPosition: Int
    ): Int {
        val pan = if (panFormatter.isFormattingEnabled()) getFormattedPan(panText) else panText

        if (panBefore.isBlank()) return pan.length

        // guard against outOfBounds exception if new pan has space added at the end
        val panCursorPosition = if (currentCursorPosition == panText.length) {
            pan.length
        } else {
            currentCursorPosition
        }

        val spaceDiffLeft =
            pan.substring(0, panCursorPosition).count { it == ' ' } - panText.substring(
                0,
                currentCursorPosition
            ).count { it == ' ' }

        val expectedCursorPosition = when {
            spaceDiffLeft > 0 -> currentCursorPosition + spaceDiffLeft
            else -> currentCursorPosition
        }

        // jump over the space is the expected cursor is on a space
        if (pan.length > expectedCursorPosition && pan[expectedCursorPosition] == ' ') {
            return expectedCursorPosition + 1
        }

        return expectedCursorPosition
    }

    /**
     * Validates the given pan and calls the validation result handler to handle the validation
     * result
     */
    private fun validate(
        pan: String,
        cardValidationRule: CardValidationRule,
        brand: RemoteCardBrand?
    ) {
        val validationState = panValidator.validate(pan, cardValidationRule, brand)
        val isValid = validationState == VALID
        val forceNotify = validationState == CARD_BRAND_NOT_ACCEPTED

        panValidationResultHandler.handleResult(isValid, forceNotify)
    }

    /**
     * Trims the given pan to the max length allowed by the given card validation rule
     *
     * @return Boolean - true if the pan has been trimmed otherwise false
     */
    private fun trimToMaxLength(cardValidationRule: CardValidationRule, pan: String): Boolean {
        val maxLength = ValidationUtil.getMaxLength(cardValidationRule)
        val expectedNumberOfSpaces = panFormatter.getExpectedNumberOfSpaces(pan)
        val totalMaxLength = maxLength + expectedNumberOfSpaces

        if (pan.length > totalMaxLength) {
            val charsToDrop = pan.length - totalMaxLength
            setText(pan.dropLast(charsToDrop), expectedCursorPosition)
            return true
        }
        return false
    }

    private fun getFormattedPan(
        panText: String,
        cardBrand: RemoteCardBrand? = findBrandForPan(panText)
    ) = panFormatter.format(panText, cardBrand)

    /**
     * Handles the card brand if it is different to the previous one.
     * This function also revalidates the cvc using the cvc validation
     * rule of the new card brand
     */
    private fun handleCardBrandChange(newCardBrands: List<RemoteCardBrand?>) {
        if (cardBrands == newCardBrands) return

        cardBrands = newCardBrands

        brandsChangedHandler.handle(cardBrands)

        // Use the first card brand from the list else pass null if the list is empty
        cvcValidationRuleManager.updateRule(getCvcValidationRule(cardBrands.firstOrNull()))

        val cvcText = cvcAccessEditText.text.toString()
        if (cvcText.isNotBlank()) {
            cvcValidator.validate(cvcText)
        }
    }

    private fun setText(text: String, cursorPosition: Int) {
        panEditText.removeTextChangedListener(this)

        // We set the text using editableText rather than using the setText() method to fix an issue
        // where the backspace key does not delete the whole text when pressed and maintained
        // on a device virtual keyboard
        val editable = panEditText.editableText
        editable.replace(0, editable.length, text, 0, text.length)

        panEditText.addTextChangedListener(this)

        // guard against outOfBounds exception in an occasional case
        // where cursorPosition is beyond the text length
        val selection = min(cursorPosition, text.length)
        panEditText.setSelection(selection)
    }

    private fun isPanRequiredLength(): Boolean {
        val pan = panEditText.editableText.toString()
        val formattedPan = pan.replace(" ", "").length
        return (formattedPan >= requiredPanLengthForCardBrands)
    }
}
