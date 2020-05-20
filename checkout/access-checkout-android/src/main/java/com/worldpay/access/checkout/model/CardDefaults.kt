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
	val pan: CardValidationRule? = null,
	val cvv: CardValidationRule? = null,
	val month: CardValidationRule? = null,
	val year: CardValidationRule? = null
)
