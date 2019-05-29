package com.worldpay.access.checkout.views

/**
 * [CardView] is an interface which represents a particular field in the payment form
 */
interface CardView {

    /**
     * The [CardViewListener] should receive updates from this [CardView] when the field has been updated or some other
     * event has happened
     */
    var cardViewListener: CardViewListener?

    /**
     * This [CardView] should return the text from the field that it is representing
     * @return the text from the field
     */
    fun getInsertedText(): String
}