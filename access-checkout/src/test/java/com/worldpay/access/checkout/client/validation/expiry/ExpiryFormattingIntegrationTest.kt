package com.worldpay.access.checkout.client.validation.expiry

import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExpiryFormattingIntegrationTest : AbstractValidationIntegrationTest() {

    @Before
    fun setup() {
        initialiseValidation()
    }

    @Test
    fun `should append forward slash after month is entered`() {
        expiryDate.setText("02")
        assertEquals("02/", expiryDate.text)
    }

    @Test
    fun `should be able to edit month independently without reformatting`() {
        expiryDate.setText("01/29")
        assertEquals("01/29", expiryDate.text)

        expiryDate.setText("0/29")
        assertEquals("0/29", expiryDate.text)

        expiryDate.setText("/29")
        assertEquals("/29", expiryDate.text)

        expiryDate.setText("1/29")
        assertEquals("1/29", expiryDate.text)
    }

    @Test
    fun `should reformat pasted new date overwriting an existing one`() {
        expiryDate.setText("01/19")
        assertEquals("01/19", expiryDate.text)

        expiryDate.setText("1299")
        assertEquals("12/99", expiryDate.text)

        expiryDate.setText("1298")
        assertEquals("12/98", expiryDate.text)

        expiryDate.setText("12/98")
        assertEquals("12/98", expiryDate.text)

        expiryDate.setText("12")
        assertEquals("12/", expiryDate.text)

        expiryDate.setText("12")
        assertEquals("12", expiryDate.text)
    }

    @Test
    fun `should be able to delete characters to empty from valid expiry date`() {
        expiryDate.setText("12/99")
        assertEquals("12/99", expiryDate.text)

        expiryDate.setText("12/9")
        assertEquals("12/9", expiryDate.text)

        expiryDate.setText("12/")
        assertEquals("12/", expiryDate.text)

        expiryDate.setText("12")
        assertEquals("12", expiryDate.text)

        expiryDate.setText("1")
        assertEquals("1", expiryDate.text)

        expiryDate.setText("")
        assertEquals("", expiryDate.text)
    }

    @Test
    fun `should be able to delete characters to empty from invalid expiry date`() {
        expiryDate.setText("13/99")
        assertEquals("13/99", expiryDate.text)

        expiryDate.setText("13/9")
        assertEquals("13/9", expiryDate.text)

        expiryDate.setText("13/")
        assertEquals("13/", expiryDate.text)

        expiryDate.setText("13")
        assertEquals("13", expiryDate.text)

        expiryDate.setText("1")
        assertEquals("1", expiryDate.text)

        expiryDate.setText("")
        assertEquals("", expiryDate.text)
    }

    @Test
    fun `should not reformat pasted value when pasted value is same as current value`() {
        expiryDate.setText("12/")
        assertEquals("12/", expiryDate.text)

        expiryDate.setText("12")
        assertEquals("12", expiryDate.text)
    }

    @Test
    fun `should be able to add characters to complete`() {
        expiryDate.setText("")
        assertEquals("", expiryDate.text)

        expiryDate.setText("1")
        assertEquals("1", expiryDate.text)

        expiryDate.setText("12")
        assertEquals("12/", expiryDate.text)

        expiryDate.setText("12/")
        assertEquals("12/", expiryDate.text)

        expiryDate.setText("12/9")
        assertEquals("12/9", expiryDate.text)

        expiryDate.setText("12/99")
        assertEquals("12/99", expiryDate.text)
    }

    @Test
    fun `should format single digits correctly - overwrite`() {
        val testMap = mapOf(
            "1" to "1",
            "2" to "02/",
            "3" to "03/",
            "4" to "04/",
            "5" to "05/",
            "6" to "06/",
            "7" to "07/",
            "8" to "08/",
            "9" to "09/"
        )

        for (entry in testMap) {
            assertEquals(entry.value, enterAndGetText(entry.key))
        }
    }

    @Test
    fun `should format single digits correctly - newly entered`() {
        val testMap = mapOf(
            "1" to "1",
            "2" to "02/",
            "3" to "03/",
            "4" to "04/",
            "5" to "05/",
            "6" to "06/",
            "7" to "07/",
            "8" to "08/",
            "9" to "09/"
        )

        for (entry in testMap) {
            expiryDate.setText("")
            assertEquals(entry.value, enterAndGetText(entry.key))
        }
    }

    @Test
    fun `should reformat when month value changes despite the separator being deleted`() {
        expiryDate.setText("02/")
        assertEquals("02/", expiryDate.text)

        expiryDate.setText("03")
        assertEquals("03/", expiryDate.text)
    }

    @Test
    fun `should format double digits correctly`() {
        val testMap = mapOf(
            "10/" to "10",
            "11/" to "11",
            "12/" to "12",
            "01/3" to "13",
            "01/4" to "14",
            "02/4" to "24"
        )

        for (entry in testMap) {
            expiryDate.setText("")
            assertEquals(entry.key, enterAndGetText(entry.value))
        }
    }

    @Test
    fun `should format triple digits correctly`() {
        val testMap = mapOf(
            "10/0" to "100",
            "11/0" to "110",
            "12/0" to "120",
            "01/33" to "133",
            "01/43" to "143",
            "02/44" to "244"
        )

        for (entry in testMap) {
            expiryDate.setText("")
            assertEquals(entry.key, enterAndGetText(entry.value))
        }
    }

    private fun enterAndGetText(string: String): String {
        expiryDate.setText(string)
        return expiryDate.text
    }
}
