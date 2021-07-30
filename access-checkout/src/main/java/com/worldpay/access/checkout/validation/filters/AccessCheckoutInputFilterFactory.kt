package com.worldpay.access.checkout.validation.filters

import android.widget.EditText

internal class AccessCheckoutInputFilterFactory {

    fun getCvcLengthFilter(panEditText: EditText?): CvcLengthFilter {
        return CvcLengthFilter(panEditText)
    }

    fun getPanNumericFilter(): PanNumericFilter {
        return PanNumericFilter()
    }

    fun getExpiryDateLengthFilter(): ExpiryDateLengthFilter {
        return ExpiryDateLengthFilter()
    }
}
