package com.worldpay.access.checkout.validation.filters

import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration

internal class LengthFilterFactory {

    fun getCvcLengthFilter(panEditText : EditText?, cardConfiguration : CardConfiguration) : CvcLengthFilter {
        return CvcLengthFilter(panEditText, cardConfiguration)
    }

    fun getPanLengthFilter(cardConfiguration : CardConfiguration) : PanLengthFilter {
        return PanLengthFilter(cardConfiguration)
    }

    fun getExpiryDateLengthFilter(cardConfiguration : CardConfiguration) : ExpiryDateLengthFilter {
        return ExpiryDateLengthFilter(cardConfiguration)
    }

}
