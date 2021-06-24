package com.worldpay.access.checkout.session.api.request

import kotlin.test.assertEquals
import org.junit.Test

class SessionRequestTest {

    @Test
    fun `should be able to set and get the cvc and merchant identity for a cvc session request`() {
        val cvc = "123"
        val identity = "MERCHANT-123"

        val cvcSessionRequest =
            CvcSessionRequest(
                cvc,
                identity
            )

        assertEquals(cvc, cvcSessionRequest.cvc)
        assertEquals(identity, cvcSessionRequest.identity)
    }

    @Test
    fun `should be able to set and get the expected details for card session request`() {
        val cardNumber = "12333333"
        val cardExpiryDate = CardSessionRequest.CardExpiryDate(10, 2050)
        val cvc = "123"
        val identity = "MERCHANT-123"

        val cvcSessionRequest =
            CardSessionRequest(
                cardNumber,
                cardExpiryDate,
                cvc,
                identity
            )

        assertEquals(cardNumber, cvcSessionRequest.cardNumber)
        assertEquals(cardExpiryDate, cvcSessionRequest.cardExpiryDate)
        assertEquals(cvc, cvcSessionRequest.cvc)
        assertEquals(identity, cvcSessionRequest.identity)
    }
}
