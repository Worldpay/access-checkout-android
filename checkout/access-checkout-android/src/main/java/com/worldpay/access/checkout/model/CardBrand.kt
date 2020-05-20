package com.worldpay.access.checkout.model

/**
 * Representation of a card brand
 *
 * @property name of the card brand
 * @property images a list of [CardBrandImage]'s
 * @property cvv validation rule for the cvv field
 * @property pan list of validations rules for the pan field
 */
data class CardBrand(
	val name: String,
	val images: List<CardBrandImage>? = emptyList(),
	val cvv: CardValidationRule? = null,
	val pan: CardValidationRule?  = null
)
