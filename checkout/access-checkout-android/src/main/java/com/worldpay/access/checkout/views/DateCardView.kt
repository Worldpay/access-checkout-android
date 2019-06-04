package com.worldpay.access.checkout.views

import android.text.InputFilter

interface DateCardView: CardView {

    fun isValidMonth(valid: Boolean)
    fun isValidYear(valid: Boolean)
    fun applyMonthLengthFilter(inputFilter: InputFilter)
    fun applyYearLengthFilter(inputFilter: InputFilter)

    fun getInsertedMonth(): String
    fun getInsertedYear(): String

    fun getMonth(): Int
    fun getYear(): Int
}