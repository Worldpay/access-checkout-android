package com.worldpay.access.checkout.model

/**
 * Representation of a card brand
 *
 * @property name of the card brand
 * @property image resource name of the card brand image
 * @property cvv validation rule for the cvv field
 * @property pans list of validations rules for the pan field
 */
data class CardBrand(
	val name: String,
	val image: String? = null,
	val cvv: CardValidationRule? = null,
	val pans: List<CardValidationRule>
)
