package com.worldpay.access.checkout.views

import android.text.InputFilter

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
     * A method to respond to validation updates
     *
     * @param valid whether this view is valid or not
     */
    fun isValid(valid: Boolean)

    /**
     * A method to respond to length updates
     *
     * @param inputFilter the length filter to apply to this card view
     */
    fun applyLengthFilter(inputFilter: InputFilter)
}