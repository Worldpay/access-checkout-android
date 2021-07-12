package com.worldpay.access.checkout.validation.filters

import android.text.InputFilter
import android.text.Spanned

internal abstract class AbstractVariableLengthFilter : InputFilter {

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val text = getTextValue(dest.toString() + source.toString())
        val maxLength = this.getMaxLength(text)
        val lengthFilter = InputFilter.LengthFilter(maxLength)
        return lengthFilter.filter(text, start, text.length, dest, dstart, dend)
    }

    abstract fun getMaxLength(source: CharSequence?): Int

    open fun getTextValue(source: String) = source
}
