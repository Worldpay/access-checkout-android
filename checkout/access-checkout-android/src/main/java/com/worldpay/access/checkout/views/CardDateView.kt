package com.worldpay.access.checkout.views

interface CardDateView: CardView {

    fun getInsertedMonth(): String
    fun getInsertedYear(): String

    fun getMonth(): Int
    fun getYear(): Int
}