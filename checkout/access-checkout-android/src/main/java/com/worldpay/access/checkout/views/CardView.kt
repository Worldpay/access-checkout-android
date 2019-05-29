package com.worldpay.access.checkout.views

interface CardView {

    var cardViewListener: CardViewListener?

    fun getInsertedText(): String
}