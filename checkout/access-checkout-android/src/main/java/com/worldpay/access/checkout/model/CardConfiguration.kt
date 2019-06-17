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
)
