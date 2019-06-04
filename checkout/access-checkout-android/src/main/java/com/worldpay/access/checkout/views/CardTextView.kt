package com.worldpay.access.checkout.views

/**
 * [CardTextView] is an interface which represents single text fields in the payment form
 */
interface CardTextView: CardView {

    /**
     * This [CardTextView] should return the text from the field
     * @return the text from the field
     */
    fun getInsertedText(): String

}