package com.worldpay.access.checkout

import com.worldpay.access.checkout.validation.CardValidator
import com.worldpay.access.checkout.views.CardViewListener

/**
 * A [Card] is the coordinator class between the view inputs, the (optional) validations of those inputs,
 * and the callback of those validation results to the [CardListener]
 *
 * See [AccessCheckoutCard] for an out-of-the-box implementation of this
 */
@Deprecated(message = "legacy")
interface Card: CardViewListener {

    /**
     * The listener to which callbacks on validation results will be sent to. See [CardListener] for details on
     * how to handle these results
     */
    var cardListener: CardListener?

    /**
     * The class responsible for validating the state of the card fields
     */
    var cardValidator: CardValidator?

    /**
     * Validates the state of the card as a whole
     * @return true if the card is fully valid, false otherwise
     */
    fun isValid(): Boolean
}
