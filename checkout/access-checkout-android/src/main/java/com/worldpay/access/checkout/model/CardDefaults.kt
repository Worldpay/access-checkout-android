package com.worldpay.access.checkout.model

/**
 * [CardDefaults] defines default validation rules for the fields
 *
 * @property pan a default pan validation rule
 * @property cvv a default cvv validation rule
 * @property month a default month validation rule
 * @property year a default year validation rule
 */
data class CardDefaults(
	val pan: CardValidationRule?,
	val cvv: CardValidationRule?,
	val month: CardValidationRule?,
	val year: CardValidationRule?
)
