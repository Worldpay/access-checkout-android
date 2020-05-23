package com.worldpay.access.checkout.views

import android.text.InputFilter
import android.text.Spanned
import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardDefaults
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CARD_DEFAULTS
import com.worldpay.access.checkout.validation.CardValidator
import com.worldpay.access.checkout.validation.ValidationResult

/**
 * [AbstractCardFieldLengthFilter] is a common abstraction class which is used by individual field implementers to restrict the length of a particular card field
 *
 * @param cardConfiguration (optional) the configuration to use for determining the length for the field
 */
sealed class AbstractCardFieldLengthFilter(private val cardConfiguration: CardConfiguration?) : InputFilter {

    internal val lengthFiltersBySizeCache: MutableMap<Int, InputFilter.LengthFilter> = mutableMapOf()

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        return dest?.let { spanned ->
            val maxLength = getMaxLength(spanned)

            return lengthFiltersBySizeCache.getOrElse(maxLength) {
                val lengthFilter = InputFilter.LengthFilter(maxLength)
                lengthFiltersBySizeCache[maxLength] = lengthFilter
                lengthFilter
            }.filter(source, start, end, dest, dstart, dend)
        }
    }

    open fun getValidationResult(field: Spanned): Pair<ValidationResult, CardBrand?> = Pair(ValidationResult(partial = true, complete = true), null)

    abstract fun getMaxLengthRule(cardBrand: CardBrand?, cardDefaults: CardDefaults): CardValidationRule

    private fun getMaxLength(spanned: Spanned): Int {
        val (_, cardBrand) = getValidationResult(spanned)
        val maxLengthRule = getMaxLengthRule(cardBrand, getCardDefaults())
        return maxLengthRule.validLengths.max() ?: Int.MAX_VALUE
    }

    private fun getCardDefaults() = cardConfiguration?.defaults ?: CARD_DEFAULTS
}

/**
 * [CVVLengthFilter] applies a length restriction to the cvv field based on the identity of the card detected during
 * input
 */
class CVVLengthFilter(
    private val cardValidator: CardValidator,
    private val panView: CardTextView?
) : AbstractCardFieldLengthFilter(cardValidator.cardConfiguration) {

    override fun getValidationResult(field: Spanned): Pair<ValidationResult, CardBrand?> =
        cardValidator.validateCVV(field.toString(), panView?.getInsertedText())

    override fun getMaxLengthRule(cardBrand: CardBrand?, cardDefaults: CardDefaults) = cardBrand?.cvv ?: cardDefaults.cvv

}

/**
 * [PANLengthFilter] applies a length restriction to the pan field based on the identity of the card detected during
 * input
 */
class PANLengthFilter(private val cardValidator: CardValidator) :
    AbstractCardFieldLengthFilter(cardValidator.cardConfiguration) {

    override fun getValidationResult(field: Spanned): Pair<ValidationResult, CardBrand?> = cardValidator.validatePAN(field.toString())

    override fun getMaxLengthRule(cardBrand: CardBrand?, cardDefaults: CardDefaults): CardValidationRule {
        if (cardBrand == null) return cardDefaults.pan
        return cardBrand.pan
    }

}

/**
 * [DateLengthFilter] applies a length restriction to the date field based on configuration defaults
 */
class DateLengthFilter(cardConfiguration: CardConfiguration): AbstractCardFieldLengthFilter(cardConfiguration) {

    override fun getMaxLengthRule(cardBrand: CardBrand?, cardDefaults: CardDefaults) = cardDefaults.month

}
