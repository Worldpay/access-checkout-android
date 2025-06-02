package com.worldpay.access.checkout.cardbin.api.response

import java.io.Serializable

/**
 * A deserialized response from a Card Bin api request
 *
 * @property brand A list of card brands (e.g., ["visa"])
 * @property fundingType The funding type of the card (e.g., "debit")
 * @property luhnCompliant Whether card is required to be luhn compliant (e.g., true or false)
 */

internal data class CardBinResponse(
    val brand: List<String>,
    val fundingType: String,
    val luhnCompliant: Boolean
) : Serializable
