package com.worldpay.access.checkout.validation.listeners.text

import android.widget.EditText
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.worldpay.access.checkout.validation.result.handler.ExpiryDateValidationResultHandler
import com.worldpay.access.checkout.validation.validators.ExpiryDateValidator
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import java.time.LocalDate
import java.time.Year
import java.util.*
import kotlin.math.abs
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class ExpiryDateTextWatcherIntegrationTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val expiryDate = EditText(context)

    private lateinit var expiryDateValidationResultHandler: ExpiryDateValidationResultHandler

    @Before
    fun setup() {
        val dateValidator = ExpiryDateValidator()
        expiryDateValidationResultHandler = mock()

        val expiryDateTextWatcher = ExpiryDateTextWatcher(
            dateValidator = dateValidator,
            expiryDateEditText = expiryDate,
            expiryDateValidationResultHandler = expiryDateValidationResultHandler,
            expiryDateSanitiser = ExpiryDateSanitiser()
        )

        expiryDate.addTextChangedListener(expiryDateTextWatcher)
    }

    @Test
    fun `should append forward slash after month is entered`() {
        expiryDate.setText("02")
        assertEquals("02/", expiryDate.text.toString())
    }

    @Test
    fun `should be able to edit month independently without reformatting`() {
        expiryDate.setText("01/29")
        assertEquals("01/29", expiryDate.text.toString())

        expiryDate.setText("0/29")
        assertEquals("0/29", expiryDate.text.toString())

        expiryDate.setText("/29")
        assertEquals("/29", expiryDate.text.toString())

        expiryDate.setText("1/29")
        assertEquals("1/29", expiryDate.text.toString())
    }

    @Test
    fun `should reformat pasted new date overwriting an existing one`() {
        expiryDate.setText("01/19")
        assertEquals("01/19", expiryDate.text.toString())

        expiryDate.setText("1299")
        assertEquals("12/99", expiryDate.text.toString())

        expiryDate.setText("1298")
        assertEquals("12/98", expiryDate.text.toString())

        expiryDate.setText("12/98")
        assertEquals("12/98", expiryDate.text.toString())

        expiryDate.setText("12")
        assertEquals("12/", expiryDate.text.toString())

        expiryDate.setText("12")
        assertEquals("12", expiryDate.text.toString())
    }

    @Test
    fun `should be able to delete characters to empty from valid expiry date`() {
        expiryDate.setText("12/99")
        assertEquals("12/99", expiryDate.text.toString())

        expiryDate.setText("12/9")
        assertEquals("12/9", expiryDate.text.toString())

        expiryDate.setText("12/")
        assertEquals("12/", expiryDate.text.toString())

        expiryDate.setText("12")
        assertEquals("12", expiryDate.text.toString())

        expiryDate.setText("1")
        assertEquals("1", expiryDate.text.toString())

        expiryDate.setText("")
        assertEquals("", expiryDate.text.toString())
    }

    @Test
    fun `should be able to delete characters to empty from invalid expiry date`() {
        expiryDate.setText("13/99")
        assertEquals("13/99", expiryDate.text.toString())

        expiryDate.setText("13/9")
        assertEquals("13/9", expiryDate.text.toString())

        expiryDate.setText("13/")
        assertEquals("13/", expiryDate.text.toString())

        expiryDate.setText("13")
        assertEquals("13", expiryDate.text.toString())

        expiryDate.setText("1")
        assertEquals("1", expiryDate.text.toString())

        expiryDate.setText("")
        assertEquals("", expiryDate.text.toString())
    }

    @Test
    fun `should not reformat pasted value when pasted value is same as current value`() {
        expiryDate.setText("12/")
        assertEquals("12/", expiryDate.text.toString())

        expiryDate.setText("12")
        assertEquals("12", expiryDate.text.toString())
    }

    @Test
    fun `should be able to add characters to complete`() {
        expiryDate.setText("")
        assertEquals("", expiryDate.text.toString())

        expiryDate.setText("1")
        assertEquals("1", expiryDate.text.toString())

        expiryDate.setText("12")
        assertEquals("12/", expiryDate.text.toString())

        expiryDate.setText("12/")
        assertEquals("12/", expiryDate.text.toString())

        expiryDate.setText("12/9")
        assertEquals("12/9", expiryDate.text.toString())

        expiryDate.setText("12/99")
        assertEquals("12/99", expiryDate.text.toString())
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
        assertEquals("02/", expiryDate.text.toString())

        expiryDate.setText("03")
        assertEquals("03/", expiryDate.text.toString())
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

    @Test
    fun `should validate expiry date as true where month and year is valid`() {
        expiryDate.setText("04/${getYear(1)}")

        verify(expiryDateValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should validate expiry date as false where year is in the past`() {
        expiryDate.setText("04/${getYear(-1)}")

        verify(expiryDateValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should validate expiry date as false where month is in the past`() {
        val monthInPast = getMonth(-1)

        if (monthInPast == "12") {
            // in the month of january, -1 on the year as well
            expiryDate.setText("${monthInPast}/${getYear(-1)}")
        } else {
            expiryDate.setText("${monthInPast}/${getYear()}")
        }

        verify(expiryDateValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should validate expiry date as false where month is invalid and year is invalid`() {
        expiryDate.setText("05/19")

        verify(expiryDateValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should validate expiry date as true where month and year is valid and current day is last day of month`() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 2020)
        calendar.set(Calendar.MONTH, 5)
        calendar.set(Calendar.DAY_OF_MONTH, 30)

        val dateValidator = ExpiryDateValidator(calendar)

        val expiryYearTextWatcher = ExpiryDateTextWatcher(
            dateValidator = dateValidator,
            expiryDateEditText = expiryDate,
            expiryDateValidationResultHandler = expiryDateValidationResultHandler,
            expiryDateSanitiser = ExpiryDateSanitiser()
        )

        val expiryDate = EditText(context)

        expiryDate.addTextChangedListener(expiryYearTextWatcher)

        expiryDate.setText("06/20")

        verify(expiryDateValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should validate expiry date as false where month is non-numeric`() {
        expiryDate.setText("ab/${getYear()}")

        verify(expiryDateValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should validate expiry date as false where year is non-numeric`() {
        expiryDate.setText("${getMonth()}/ab")

        verify(expiryDateValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should not send any validation result where month is not provided`() {
        expiryDate.setText(getYear())

        verify(expiryDateValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should not send any validation result where year is not provided`() {
        expiryDate.setText(getMonth())

        verify(expiryDateValidationResultHandler).handleResult(false)
    }

    private fun enterAndGetText(string: String) : String {
        expiryDate.setText(string)
        return expiryDate.text.toString()
    }

    private fun getMonth(offset: Int = 0): String {
        var month = LocalDate.now().month.value.toString()

        if (offset < 0) {
            month = LocalDate.now().minusMonths(abs(offset).toLong()).monthValue.toString()
        }

        if (offset > 0) {
            month = LocalDate.now().plusMonths(offset.toLong()).monthValue.toString()
        }

        if (month.length == 1) month = String.format("0%s", month)

        return month
    }

    private fun getYear(offset: Int = 0): String {
        val currentYear = Year.now().value
        return (currentYear + offset).toString().drop(2)
    }

}
