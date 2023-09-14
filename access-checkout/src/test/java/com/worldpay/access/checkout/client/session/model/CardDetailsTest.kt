package com.worldpay.access.checkout.client.session.model
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.worldpay.access.checkout.ui.AccessEditText
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import org.junit.Test
class CardDetailsTest {


    @Test
    fun `should be able to create a card details instance using the builder - expiry date with separator`() {

        val pan = mock<AccessEditText>()
        whenever(pan.text).thenReturn("120392895018742508243")
        val expiryDate = mock<AccessEditText>() {
            on {text} doReturn "11/20"
        }
        val cvc = mock<AccessEditText>() {
            on {text} doReturn "123"
        }

        val cardDetails = CardDetails.Builder()
            .pan(pan)
            .expiryDate(expiryDate)
            .cvc(cvc)
            .build()

        assertNotNull(cardDetails)
        assertEquals("120392895018742508243", cardDetails.pan)
        assertEquals(11, cardDetails.expiryDate?.month)
        assertEquals(2020, cardDetails.expiryDate?.year)
        assertEquals("123", cardDetails.cvc)
    }

    @Test
    fun `should be able to create a card details instance using the builder - formatted pan has white spaces stripped`() {
        val pan = mock<AccessEditText>()
        whenever(pan.text).thenReturn("1203 9289 5018 7425 08243")
        val expiryDate = mock<AccessEditText>() {
            on {text} doReturn "11/20"
        }
        val cvc = mock<AccessEditText>() {
            on {text} doReturn "123"
        }

        val cardDetails = CardDetails.Builder()
            .pan(pan)
            .expiryDate(expiryDate)
            .cvc(cvc)
            .build()

        assertNotNull(cardDetails)
        assertEquals("120392895018742508243", cardDetails.pan)
        assertEquals(11, cardDetails.expiryDate?.month)
        assertEquals(2020, cardDetails.expiryDate?.year)
        assertEquals("123", cardDetails.cvc)
    }

    @Test
    fun `should be able to parse expiry date without separator`() {
        val pan = mock<AccessEditText>()
        whenever(pan.text).thenReturn("120392895018742508243")
        val expiryDate = mock<AccessEditText>() {
            on {text} doReturn "1120"
        }
        val cvc = mock<AccessEditText>() {
            on {text} doReturn "123"
        }

        val cardDetails = CardDetails.Builder()
            .pan(pan)
            .expiryDate(expiryDate)
            .cvc(cvc)
            .build()

        assertNotNull(cardDetails)
        assertEquals("120392895018742508243", cardDetails.pan)
        assertEquals(11, cardDetails.expiryDate?.month)
        assertEquals(2020, cardDetails.expiryDate?.year)
        assertEquals("123", cardDetails.cvc)
    }

    @Test
    fun `should throw format exception where expiry date is not length of 4 characters`() {
        val pan = mock<AccessEditText>()
        whenever(pan.text).thenReturn("120392895018742508243")
        val expiryDate = mock<AccessEditText>() {
            on {text} doReturn "11200"
        }
        val cvc = mock<AccessEditText>() {
            on {text} doReturn "123"
        }

        val exception = assertFailsWith<IllegalArgumentException> {
            CardDetails.Builder()
                .pan(pan)
                .expiryDate(expiryDate)
                .cvc(cvc)
                .build()
        }

        assertEquals("expecting expiry date in format MM/YY or MMYY but found 11200", exception.message)
    }

    @Test
    fun `should throw format exception where expiry date has non numerics`() {
        val pan = mock<AccessEditText>()
        whenever(pan.text).thenReturn("120392895018742508243")
        val expiryDate = mock<AccessEditText>() {
            on {text} doReturn "abcd"
        }
        val cvc = mock<AccessEditText>() {
            on {text} doReturn "123"
        }

        val exception = assertFailsWith<IllegalArgumentException> {
            CardDetails.Builder()
                .pan(pan)
                .expiryDate(expiryDate)
                .cvc(cvc)
                .build()
        }

        assertEquals("expecting expiry date in format MM/YY or MMYY but found abcd", exception.message)
    }

    @Test
    fun `should be able to create a card details instance without providing pan`() {
        val expiryDate = mock<AccessEditText>() {
            on {text} doReturn "1120"
        }
        val cvc = mock<AccessEditText>() {
            on {text} doReturn "123"
        }

        val cardDetails = CardDetails.Builder()
            .expiryDate(expiryDate)
            .cvc(cvc)
            .build()

        assertNotNull(cardDetails)
    }

    @Test
    fun `should be able to create a card details instance without providing expiry date`() {
        val pan = mock<AccessEditText>()
        whenever(pan.text).thenReturn("120392895018742508243")
        val cvc = mock<AccessEditText>() {
            on {text} doReturn "123"
        }

        val cardDetails = CardDetails.Builder()
            .pan(pan)
            .cvc(cvc)
            .build()

        assertNotNull(cardDetails)
    }

    @Test
    fun `should be able to create a card details instance without providing cvc`() {
        val pan = mock<AccessEditText>()
        whenever(pan.text).thenReturn("120392895018742508243")
        val expiryDate = mock<AccessEditText>() {
            on {text} doReturn "11200"
        }

        val cardDetails = CardDetails.Builder()
            .pan(pan)
            .expiryDate(expiryDate)
            .build()

        assertNotNull(cardDetails)
    }
}
