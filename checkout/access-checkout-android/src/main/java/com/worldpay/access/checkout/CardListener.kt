package com.worldpay.access.checkout

import android.text.InputFilter
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.validation.ValidationResult

/**
 * The purpose of this interface is that it will receive updates of [ValidationResult]'s and [CardBrand]'s from the [Card]
 * when the fields are being edited or have completed editing. Implementations of this interface can decide how to apply
 * those validation results to the card fields or any other appropriate UI elements in the application
 *
 * A [ValidationResult] can either be partially and/or completely valid and/or invalid. Implementers can choose how to style
 * their payment forms based on the validation state of the fields at the moment the user is filling out their card data
 *
 * A [CardBrand] is an object that represents the card scheme that is linked to the pan that is being entered by the user.
 * Implementers can choose how to style their pan field based on this information, such as showing a small icon for that
 * card brand.
 *
 * A [InputFilter] can be attached to a field to restrict the length of that field based on the current input.
 * For example, a card brand may have a particular limit on the number of characters it will accept and therefore applying the length
 * filter will help reduce human user error on that field by not allowing them to type more characters than the maximum
 * length for that card brand
 */
interface CardListener {

  /**
   * Receives an update once any validation has finished after an update to the pan field
   *
   * @param validationResult The validation result of this pan field update
   * @param cardBrand (Optional) The card brand that was associated with this pan field update
   * @param lengthFilter (Optional) The length filter that can be applied to the pan field as a result of this update
   */
  fun onPANUpdateValidationResult(validationResult: ValidationResult, cardBrand: CardBrand?, lengthFilter: InputFilter?)

  /**
   * Receives an update once any validation has finished after the pan field has been completed
   *
   * @param validationResult The validation result for this pan field
   * @param cardBrand (Optional) The card brand that was associated with this pan field update
   */
  fun onPANEndUpdateValidationResult(validationResult: ValidationResult, cardBrand: CardBrand?)

  /**
   * Receives an update once any validation has finished after an update to the cvv field
   *
   * @param validationResult The validation result of this cvv field update
   * @param lengthFilter (Optional) The length filter that can be applied to the cvv field as a result of this update
   */
  fun onCVVUpdateValidationResult(validationResult: ValidationResult, lengthFilter: InputFilter?)

  /**
   * Receives an update once any validation has finished after the cvv field has been completed
   *
   * @param validationResult The validation result of this cvv field
   */
  fun onCVVEndUpdateValidationResult(validationResult: ValidationResult)

  /**
   * Receives an update once any validation has finished after an update to the date fields
   *
   * @param monthValidationResult The validation result of this month field update
   * @param yearValidationResult The validation result of this year field update
   * @param monthLengthFilter The month length filter that can be applied to the month field as a result of this update
   * @param yearLengthFilter The year length filter that can be applied to the year field as a result of this update
   */
  fun onDateUpdateValidationResult(
    monthValidationResult: ValidationResult?, yearValidationResult: ValidationResult?,
    monthLengthFilter: InputFilter, yearLengthFilter: InputFilter
  )

  /**
   * Receives an update once any validation has finished after the date fields have been completed
   *
   * @param validationResult The validation result of this date field
   */
  fun onDateEndUpdateValidationResult(validationResult: ValidationResult)

  /**
   * Receives an update on all change events with a validation result for the entire card
   *
   * @param validationResult The validation result for this entire card
   */
  fun onValidationResult(validationResult: ValidationResult)
}
