package com.worldpay.access.checkout.client.card

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CardDetailsBuilderTest {

    @Test
    fun `should be able to create a card details instance using the builder`() {
        val cardDetails = CardDetailsBuilder()
            .pan("120392895018742508243")
            .expiryDate(11, 20)
            .cvv("123")
            .build()

        assertNotNull(cardDetails)
        assertEquals("120392895018742508243", cardDetails.pan)
        assertEquals(11, cardDetails.expiryDate?.month)
        assertEquals(20, cardDetails.expiryDate?.year)
        assertEquals("123", cardDetails.cvv)
    }

    @Test
    fun `should be able to create a card details instance without providing pan`() {
        val cardDetails = CardDetailsBuilder()
            .expiryDate(11, 20)
            .cvv("123")
            .build()

        assertNotNull(cardDetails)
    }

    @Test
    fun `should be able to create a card details instance without providing expiry date`() {
        val cardDetails = CardDetailsBuilder()
            .pan("120392895018742508243")
            .cvv("123")
            .build()

        assertNotNull(cardDetails)
    }

    @Test
    fun `should be able to create a card details instance without providing cvv`() {
        val cardDetails = CardDetailsBuilder()
            .pan("120392895018742508243")
            .expiryDate(11, 20)
            .build()

        assertNotNull(cardDetails)
    }

}