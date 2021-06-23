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
        val maxLength = this.getMaxLength(dest.toString() + source.toString())
        val lengthFilter = InputFilter.LengthFilter(maxLength)
        return lengthFilter.filter(source, start, end, dest, dstart, dend)
    }

    abstract fun getMaxLength(source: CharSequence?): Int
}
