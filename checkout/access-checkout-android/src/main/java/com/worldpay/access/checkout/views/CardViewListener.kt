package com.worldpay.access.checkout.views

/**
 * [CardViewListener] is the interface which will listen to updates when fields have changed or have finished updating
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

}
