package com.worldpay.access.checkout.views

/**
 * [CardDateView] is an interface which represents the expiry date field in the payment form
 */
interface CardDateView: CardView {

    /**
     * This [CardDateView] should return the text from the month field
     * @return the text from the month field
     */
    fun getInsertedMonth(): String

    /**
     * This [CardDateView] should return the text from the year field
     * @return the text from the year field
     */
    fun getInsertedYear(): String

    /**
     * This [CardDateView] should return the month field as an integer
     * @return the month field as an int
     */
    fun getMonth(): Int

    /**
     * This [CardDateView] should return the year field as an integer
     * @return the year field as an int
     */
    fun getYear(): Int
}