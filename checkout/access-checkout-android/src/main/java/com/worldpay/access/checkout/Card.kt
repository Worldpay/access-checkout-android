package com.worldpay.access.checkout

import com.worldpay.access.checkout.validation.CardValidator
import com.worldpay.access.checkout.views.CardViewListener

interface Card: CardViewListener {

    var cardListener: CardListener?
    var cardValidator: CardValidator?

    fun isValid(): Boolean
}