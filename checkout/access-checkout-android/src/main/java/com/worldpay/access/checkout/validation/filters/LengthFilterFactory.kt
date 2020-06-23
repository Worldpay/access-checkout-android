package com.worldpay.access.checkout.validation.filters

import android.widget.EditText

internal class LengthFilterFactory {

    fun getCvcLengthFilter(panEditText : EditText?) : CvcLengthFilter {
        return CvcLengthFilter(panEditText)
    }

    fun getPanLengthFilter() : PanLengthFilter {
        return PanLengthFilter()
    }

    fun getExpiryDateLengthFilter() : ExpiryDateLengthFilter {
        return ExpiryDateLengthFilter()
    }

}
