package com.worldpay.access.checkout.api.session

import com.worldpay.access.checkout.session.api.CVVSessionRequest
import com.worldpay.access.checkout.session.api.CardSessionRequest
import org.junit.Test
import kotlin.test.assertEquals

class SessionRequestTest {

    @Test
    fun `should be able to set and get the cvv and merchant identity for a cvv session request`() {
        val cvv = "123"
        val identity = "MERCHANT-123"

        val cvvSessionRequest =
            CVVSessionRequest(
                cvv,
                identity
            )

        assertEquals(cvv, cvvSessionRequest.cvv)
        assertEquals(identity, cvvSessionRequest.identity)
    }

    @Test
    fun `should be able to set and get the expected details for card session request`() {
        val cardNumber = "12333333"
        val cardExpiryDate = CardSessionRequest.CardExpiryDate(10, 2050)
        val cvv = "123"
        val identity = "MERCHANT-123"

        val cvvSessionRequest =
            CardSessionRequest(
                cardNumber,
                cardExpiryDate,
                cvv,
                identity
            )

        assertEquals(cardNumber, cvvSessionRequest.cardNumber)
        assertEquals(cardExpiryDate, cvvSessionRequest.cardExpiryDate)
        assertEquals(cvv, cvvSessionRequest.cvv)
        assertEquals(identity, cvvSessionRequest.identity)
    }

}