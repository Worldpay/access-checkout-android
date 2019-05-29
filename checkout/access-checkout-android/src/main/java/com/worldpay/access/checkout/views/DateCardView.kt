package com.worldpay.access.checkout.views

/**
 * [DateCardView] is an interface which represents the expiry date field in the payment form
 */
interface DateCardView: CardView {

    /**
     * This [DateCardView] should return the text from the month field
     * @return the text from the month field
     */
    fun getInsertedMonth(): String

    /**
     * This [DateCardView] should return the text from the year field
     * @return the text from the year field
     */
    fun getInsertedYear(): String

    /**
     * This [DateCardView] should return the month field as an integer
     * @return the month field as an int
     */
    fun getMonth(): Int

    /**
     * This [DateCardView] should return the year field as an integer
     * @return the year field as an int
     */
    fun getYear(): Int
}