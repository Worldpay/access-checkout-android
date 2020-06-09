package com.worldpay.access.checkout.validation.filters

import android.text.InputFilter
import android.text.Spanned
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.CardBrandUtils.findBrandForPan
import com.worldpay.access.checkout.validation.utils.ValidationRuleHelper.getCvvValidationRule
import com.worldpay.access.checkout.validation.utils.ValidationRuleHelper.getMaxLength
import com.worldpay.access.checkout.validation.utils.ValidationRuleHelper.getPanValidationRule

internal abstract class VariableLengthFilter: InputFilter {

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val maxLength = this.getMaxLength(source)
        val lengthFilter = InputFilter.LengthFilter(maxLength)
        return lengthFilter.filter(source, start, end, dest, dstart, dend)
    }

    abstract fun getMaxLength(source: CharSequence?): Int

}

internal class CvvLengthFilter(
    private val panEditText: EditText?,
    private val cardConfiguration: CardConfiguration
): VariableLengthFilter() {

    override fun getMaxLength(source: CharSequence?): Int {
        var validationRule = cardConfiguration.defaults.cvv
        if (panEditText != null) {
            validationRule = getCvvValidationRule(panEditText.text.toString(), cardConfiguration)
        }
        return getMaxLength(validationRule)
    }

}

internal class PanLengthFilter(private val cardConfiguration: CardConfiguration): VariableLengthFilter() {

    override fun getMaxLength(source: CharSequence?): Int {
        val cardBrand = findBrandForPan(cardConfiguration, source.toString())
        val cardValidationRule = getPanValidationRule(cardBrand, cardConfiguration)
        return getMaxLength(cardValidationRule)
    }

}

internal class ExpiryMonthLengthFilter(private val cardConfiguration: CardConfiguration): VariableLengthFilter() {

    override fun getMaxLength(source: CharSequence?): Int {
        return getMaxLength(cardConfiguration.defaults.month)
    }

}

internal class ExpiryYearLengthFilter(private val cardConfiguration: CardConfiguration): VariableLengthFilter() {

    override fun getMaxLength(source: CharSequence?): Int {
        return getMaxLength(cardConfiguration.defaults.year)
    }

}
