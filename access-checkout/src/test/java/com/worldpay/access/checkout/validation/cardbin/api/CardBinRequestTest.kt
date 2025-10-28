package com.worldpay.access.checkout.validation.cardbin.api

import kotlin.test.assertEquals
import org.junit.Test

class CardBinRequestTest {

    @Test
    fun `should be able to set and get the card number and checkout id for a card bin request`() {
        val cardNumber = "444433332222"
        val checkoutId = "some-checkout-id"

        val cardBinRequest =
            CardBinRequest(
                cardNumber,
                checkoutId
            )

        assertEquals(cardNumber, cardBinRequest.cardNumber)
        assertEquals(checkoutId, cardBinRequest.checkoutId)
    }
}
