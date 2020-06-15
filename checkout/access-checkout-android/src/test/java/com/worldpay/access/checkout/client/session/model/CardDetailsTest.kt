package com.worldpay.access.checkout.client.session.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CardDetailsTest {

    @Test
    fun `should be able to create a card details instance using the builder - expiry date with separator`() {
        val cardDetails = CardDetails.Builder()
            .pan("120392895018742508243")
            .expiryDate("11/20")
            .cvv("123")
            .build()

        assertNotNull(cardDetails)
        assertEquals("120392895018742508243", cardDetails.pan)
        assertEquals(11, cardDetails.expiryDate?.month)
        assertEquals(20, cardDetails.expiryDate?.year)
        assertEquals("123", cardDetails.cvv)
    }

    @Test
    fun `should be able to parse expiry date without separator`() {
        val cardDetails = CardDetails.Builder()
            .pan("120392895018742508243")
            .expiryDate("1120")
            .cvv("123")
            .build()

        assertNotNull(cardDetails)
        assertEquals("120392895018742508243", cardDetails.pan)
        assertEquals(11, cardDetails.expiryDate?.month)
        assertEquals(20, cardDetails.expiryDate?.year)
        assertEquals("123", cardDetails.cvv)
    }

    @Test
    fun `should throw format exception where expiry date is not length of 4 characters`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CardDetails.Builder()
                .pan("120392895018742508243")
                .expiryDate("11200")
                .cvv("123")
                .build()
        }

        assertEquals("expecting expiry date in format MM/YY but found 11200", exception.message)
    }

    @Test
    fun `should throw format exception where expiry date has non numerics`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CardDetails.Builder()
                .pan("120392895018742508243")
                .expiryDate("abcd")
                .cvv("123")
                .build()
        }

        assertEquals("expecting expiry date in format MM/YY but found abcd", exception.message)
    }

    @Test
    fun `should be able to create a card details instance without providing pan`() {
        val cardDetails = CardDetails.Builder()
            .expiryDate("1120")
            .cvv("123")
            .build()

        assertNotNull(cardDetails)
    }

    @Test
    fun `should be able to create a card details instance without providing expiry date`() {
        val cardDetails = CardDetails.Builder()
            .pan("120392895018742508243")
            .cvv("123")
            .build()

        assertNotNull(cardDetails)
    }

    @Test
    fun `should be able to create a card details instance without providing cvv`() {
        val cardDetails = CardDetails.Builder()
            .pan("120392895018742508243")
            .expiryDate("1120")
            .build()

        assertNotNull(cardDetails)
    }

}
