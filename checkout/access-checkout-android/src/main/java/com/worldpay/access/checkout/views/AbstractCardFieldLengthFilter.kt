package com.worldpay.access.checkout.views

import android.text.InputFilter
import android.text.Spanned
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.model.CardDefaults
import com.worldpay.access.checkout.model.CardValidationRule
import com.worldpay.access.checkout.validation.CardBrandUtils
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
        return dest?.let {
            val result = getValidationResult(it)
            val maxLength = getMaxForIdentifiedBrand(result, it) ?: getMaxForDefaultRules() ?: Int.MAX_VALUE

            return lengthFiltersBySizeCache.getOrElse(maxLength) {
                val lengthFilter = InputFilter.LengthFilter(maxLength)
                lengthFiltersBySizeCache[maxLength] = lengthFilter
                lengthFilter
            }.filter(source, start, end, dest, dstart, dend)
        }
    }

    private fun getMaxForDefaultRules(): Int? {
        return ruleSelectorForDefaults(cardConfiguration?.defaults)?.let { getValueToUseForRule(it)?.max() }
    }

    private fun getMaxForIdentifiedBrand(result: Pair<ValidationResult, CardBrand?>, spanned: Spanned): Int? {
        return result.second?.let {
            ruleSelectorForCardBrand(it, spanned)?.let { rule -> getValueToUseForRule(rule)?.max() }
        }
    }

    private fun getValueToUseForRule(cardValidationRule: CardValidationRule): List<Int>? {
        return cardValidationRule.validLengths
    }

    open fun getValidationResult(field: Spanned): Pair<ValidationResult, CardBrand?> = Pair(ValidationResult(partial = true, complete = true), null)

    open fun ruleSelectorForCardBrand(cardBrand: CardBrand, field: Spanned): CardValidationRule? = null

    abstract fun ruleSelectorForDefaults(cardDefaults: CardDefaults?): CardValidationRule?
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

    override fun ruleSelectorForCardBrand(cardBrand: CardBrand, field: Spanned): CardValidationRule? = cardBrand.cvv

    override fun ruleSelectorForDefaults(cardDefaults: CardDefaults?): CardValidationRule? = cardDefaults?.cvv

}

/**
 * [PANLengthFilter] applies a length restriction to the pan field based on the identity of the card detected during
 * input
 */
class PANLengthFilter(private val cardValidator: CardValidator) :
    AbstractCardFieldLengthFilter(cardValidator.cardConfiguration) {

    override fun getValidationResult(field: Spanned): Pair<ValidationResult, CardBrand?> =
        cardValidator.validatePAN(field.toString())

    override fun ruleSelectorForCardBrand(cardBrand: CardBrand, field: Spanned): CardValidationRule? =
        CardBrandUtils.cardValidationRule(cardBrand, field.toString())

    override fun ruleSelectorForDefaults(cardDefaults: CardDefaults?): CardValidationRule? = cardDefaults?.pan

}

/**
 * [DateLengthFilter] applies a length restriction to the date field based on configuration defaults
 */
class DateLengthFilter(cardConfiguration: CardConfiguration): AbstractCardFieldLengthFilter(cardConfiguration) {
    override fun ruleSelectorForDefaults(cardDefaults: CardDefaults?): CardValidationRule? = cardDefaults?.month ?: cardDefaults?.year

}
