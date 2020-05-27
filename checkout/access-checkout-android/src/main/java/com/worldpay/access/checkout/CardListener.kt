package com.worldpay.access.checkout

import android.text.InputFilter
import com.worldpay.access.checkout.api.configuration.CardBrand
import com.worldpay.access.checkout.views.CardView

/**
 * The purpose of this interface is that it will receive updates of validations and [CardBrand]'s from the [Card]
 * when the fields are being edited or have completed editing. Implementations of this interface can decide how to apply
 * those validation results to the card fields or any other appropriate UI elements in the application

 * A [CardBrand] is an object that represents the card scheme that is linked to the pan that is being entered by the user.
 * Implementers can choose how to style their pan field based on this information, such as showing a small icon for that
 * card brand.
 *
 * An [InputFilter] can be attached to a field to restrict the length of that field based on the current input. For example,
 * a card brand may have a particular limit on the number of characters it will accept. Therefore applying the length filter
 * will help reduce human user error by not allowing the user to type more characters than is allowed for that field.
 */
interface CardListener {

    /**
     * Receives an update once any validations have finished after an update or event has happened on the card field
     *
     * @param cardView The card view to which this validation result applies to
     * @param valid Whether the state of the card view is valid or not
     */
    fun onUpdate(cardView: CardView, valid: Boolean)

    /**
     * Receives an update of any length filters to apply to a card view
     *
     * @param cardView The card view to which this input filter applies to
     * @param inputFilter The length filter that can be applied to the card field as a result of this update
     */
    fun onUpdateLengthFilter(cardView: CardView, inputFilter: InputFilter)

    /**
     * Receives an update of an identified card brand
     *
     * @param cardBrand (Optional) The card brand, if identified, that is associated with the card
     */
    fun onUpdateCardBrand(cardBrand: CardBrand?)

}
