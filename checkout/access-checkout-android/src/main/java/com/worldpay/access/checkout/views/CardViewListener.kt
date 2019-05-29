package com.worldpay.access.checkout.views

/**
 * [CardViewListener] is the interface which will listen to updates when fields have changed or have finished updating
 *
 */
interface CardViewListener {

    /**
     * Method to handle text changes to the pan field
     * The pan field should call this method on any text changes
     * @param pan The current input of the pan field
     */
    fun onUpdatePAN(pan: String)

    /**
     * Method to handle the completion of the pan field
     * The pan field should call this method when the focus has changed to another field
     * @param pan The current input of the pan field
     */
    fun onEndUpdatePAN(pan: String)

    /**
     * Method to handle text changes to the cvv field
     * The cvv field should call this method on any text changes
     * @param cvv The current input of the pan field
     */
    fun onUpdateCVV(cvv: String)

    /**
     * Method to handle the completion of the cvv field
     * The cvv field should call this method when the focus has changed to another field
     * @param cvv The current input of the pan field
     */
    fun onEndUpdateCVV(cvv: String)


    /**
     * Method to handle text changes to the date field
     * The date field should call this method on any text changes to either the month or year
     * @param month (Optional) The current input of the month field
     * @param year (Optional) The current input of the year field
     */
    fun onUpdateDate(month: String?, year: String?)

    /**
     * Method to handle the completion of the date field
     * The date field should call this method when the focus has changed to another field
     * @param month (Optional) The current input of the month field
     * @param year (Optional) The current input of the year field
     */
    fun onEndUpdateDate(month: String?, year: String?)

}
