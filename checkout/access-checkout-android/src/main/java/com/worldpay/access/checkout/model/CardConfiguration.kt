package com.worldpay.access.checkout.model

/**
 * Stores the configuration for a card for use by the card validation logic
 *
 * @property brands a list of card brand configuration
 * @property defaults a list of default configuration to use
 */
data class CardConfiguration(
	val brands: List<CardBrand>? = null,
	val defaults: CardDefaults? = null
) {

	/**
	 * Defines whether this card configuration is empty
	 *
	 * @return true if it is, false otherwise
	 */
    fun isEmpty(): Boolean = brands == null && defaults == null

	companion object {
		@JvmStatic
		fun empty() = CardConfiguration()
	}
}
