package com.worldpay.access.checkout.validation.filters

import android.text.InputFilter
import android.text.Spanned
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getCvvValidationRule
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getMaxLength
import com.worldpay.access.checkout.validation.utils.ValidationUtil.getPanValidationRule

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
            val cardBrand = findBrandForPan(cardConfiguration, panEditText.text.toString())
            validationRule = getCvvValidationRule(cardBrand, cardConfiguration)
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
