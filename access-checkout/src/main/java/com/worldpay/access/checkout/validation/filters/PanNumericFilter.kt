package com.worldpay.access.checkout.validation.filters

import android.text.Spanned

internal class PanNumericFilter : AccessCheckoutInputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence {
        return source
    }
}
