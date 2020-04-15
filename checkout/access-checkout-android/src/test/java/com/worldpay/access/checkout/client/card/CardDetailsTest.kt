package com.worldpay.access.checkout.client.card

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CardDetailsTest {

    private val pan: String = "123042935324234"
    private val expiryDate: ExpiryDate = ExpiryDate(11, 20)
    private val cvv: String = "123"

    @Test
    fun `should be able to create instance of card details`() {
        val cardDetails = CardDetails(
            pan = pan,
            expiryDate = expiryDate,
            cvv = cvv
        )

        assertNotNull(cardDetails)
        assertEquals(pan, cardDetails.pan)
        assertEquals(expiryDate, cardDetails.expiryDate)
        assertEquals(cvv, cardDetails.cvv)
    }

    @Test
    fun `should be able to create instance of card details with no pan`() {
        val cardDetails = CardDetails(
            pan = null,
            expiryDate = expiryDate,
            cvv = cvv
        )

        assertNotNull(cardDetails)
        assertNull(cardDetails.pan)
        assertEquals(expiryDate, cardDetails.expiryDate)
        assertEquals(cvv, cardDetails.cvv)
    }

    @Test
    fun `should be able to create instance of card details with no expiryDate`() {
        val cardDetails = CardDetails(
            pan = pan,
            expiryDate = null,
            cvv = cvv
        )

        assertNotNull(cardDetails)
        assertNull(cardDetails.expiryDate)
        assertEquals(pan, cardDetails.pan)
        assertEquals(cvv, cardDetails.cvv)
    }

    @Test
    fun `should be able to create instance of card details with no cvv`() {
        val cardDetails = CardDetails(
            pan = pan,
            expiryDate = expiryDate,
            cvv = null
        )

        assertNotNull(cardDetails)
        assertNull(cardDetails.cvv)
        assertEquals(pan, cardDetails.pan)
        assertEquals(expiryDate, cardDetails.expiryDate)
    }

}