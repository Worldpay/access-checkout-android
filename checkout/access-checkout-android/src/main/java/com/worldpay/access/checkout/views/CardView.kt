package com.worldpay.access.checkout.views

import android.text.InputFilter

interface CardView {

    var cardViewListener: CardViewListener?

    fun isValid(valid: Boolean)
    fun applyLengthFilter(inputFilter: InputFilter)
}