package com.worldpay.access.checkout.views

interface CardViewListener {

    fun onUpdatePAN(pan: String)
    fun onEndUpdatePAN(pan: String)

    fun onUpdateCVV(cvv: String)
    fun onEndUpdateCVV(cvv: String)

    fun onUpdateDate(month: String?, year: String?)
    fun onEndUpdateDate(month: String?, year: String?)

}
