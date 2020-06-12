package com.worldpay.access.checkout.validation

/**
 * A [ValidationResult] can be partially and completely valid and/or invalid.
 *
 * Being [partial]ly valid refers to a field currently being in a valid partial state, i.e. where it is currently being edited
 * by the user, and having passed all validation checks for a partial field. An example might be where a user is
 * editing their pan field, and we have identified their card as a Visa card, but they have not yet filled out all the 
 * digits to meet the minimum required length for a Visa card. In this case, the card is partially valid but not yet completely valid.
 *
 * Being [complete]ly valid refers to a field in a complete and valid state and ready to be
 * used in the generate session request
 */
@Deprecated(message = "legacy")
data class ValidationResult(val partial: Boolean, val complete: Boolean)
