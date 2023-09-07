package com.worldpay.access.checkout.validation.filters

import android.widget.EditText
import com.worldpay.access.checkout.ui.AccessEditText

internal class AccessCheckoutInputFilterFactory {

    fun getCvcLengthFilter(panAccessEditText: AccessEditText?): CvcLengthFilter {
        return CvcLengthFilter(panAccessEditText)
    }

    fun getPanNumericFilter(): PanNumericFilter {
        return PanNumericFilter()
    }

    fun getExpiryDateLengthFilter(): ExpiryDateLengthFilter {
        return ExpiryDateLengthFilter()
    }
}
