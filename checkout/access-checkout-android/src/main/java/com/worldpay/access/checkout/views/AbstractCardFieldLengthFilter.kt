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

sealed class AbstractCardFieldLengthFilter(private val cardConfiguration: CardConfiguration) : InputFilter {

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
        return ruleSelectorForDefaults(cardConfiguration.defaults)?.let { getValueToUseForRule(it) }
    }

    private fun getMaxForIdentifiedBrand(result: Pair<ValidationResult, CardBrand?>, spanned: Spanned): Int? {
        return result.second?.let {
            ruleSelectorForCardBrand(it, spanned)?.let { rule -> getValueToUseForRule(rule) }
        }
    }

    private fun getValueToUseForRule(cardValidationRule: CardValidationRule): Int? {
        return cardValidationRule.maxLength ?: cardValidationRule.validLength ?: cardValidationRule.minLength
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
    cardConfiguration: CardConfiguration,
    private val panView: CardView
) : AbstractCardFieldLengthFilter(cardConfiguration) {

    override fun getValidationResult(field: Spanned): Pair<ValidationResult, CardBrand?> =
        cardValidator.validateCVV(field.toString(), panView.getInsertedText())

    override fun ruleSelectorForCardBrand(cardBrand: CardBrand, field: Spanned): CardValidationRule? = cardBrand.cvv

    override fun ruleSelectorForDefaults(cardDefaults: CardDefaults?): CardValidationRule? = cardDefaults?.cvv

}

/**
 * [PANLengthFilter] applies a length restriction to the pan field based on the identity of the card detected during
 * input
 */
class PANLengthFilter(private val cardValidator: CardValidator, cardConfiguration: CardConfiguration) :
    AbstractCardFieldLengthFilter(cardConfiguration) {

    override fun getValidationResult(field: Spanned): Pair<ValidationResult, CardBrand?> =
        cardValidator.validatePAN(field.toString())

    override fun ruleSelectorForCardBrand(cardBrand: CardBrand, field: Spanned): CardValidationRule? =
        CardBrandUtils.cardValidationRule(cardBrand, field.toString())

    override fun ruleSelectorForDefaults(cardDefaults: CardDefaults?): CardValidationRule? = cardDefaults?.pan

}

/**
 * [MonthLengthFilter] applies a length restriction to the month field based on configuration defaults
 */
class MonthLengthFilter(cardConfiguration: CardConfiguration): AbstractCardFieldLengthFilter(cardConfiguration) {

    override fun ruleSelectorForDefaults(cardDefaults: CardDefaults?): CardValidationRule? = cardDefaults?.month
}

/**
 * [YearLengthFilter] applies a length restriction to the year field based on configuration defaults
 */
class YearLengthFilter(cardConfiguration: CardConfiguration): AbstractCardFieldLengthFilter(cardConfiguration) {

    override fun ruleSelectorForDefaults(cardDefaults: CardDefaults?): CardValidationRule? = cardDefaults?.year
}