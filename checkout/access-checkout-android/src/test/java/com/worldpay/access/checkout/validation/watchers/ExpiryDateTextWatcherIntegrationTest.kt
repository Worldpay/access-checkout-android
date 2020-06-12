package com.worldpay.access.checkout.validation.watchers

import android.widget.EditText
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.worldpay.access.checkout.validation.result.ExpiryDateValidationResultHandler
import com.worldpay.access.checkout.validation.validators.NewDateValidator
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
        val dateValidator = NewDateValidator()
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
    fun `should format single digits correctly`() {
        val testMap = mapOf(
            "1" to "1",
            "02/" to "2",
            "03/" to "3",
            "04/" to "4",
            "05/" to "5",
            "06/" to "6",
            "07/" to "7",
            "08/" to "8",
            "09/" to "9"
        )

        for (entry in testMap) {
            expiryDate.setText("")
            assertEquals(entry.key, enterAndGetText(entry.value))
        }
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
        expiryDate.setText("${getMonth(-1)}/${getYear()}")

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

        val dateValidator = NewDateValidator(calendar)

        val expiryYearTextWatcher = ExpiryDateTextWatcher(
            dateValidator = dateValidator,
            expiryDateEditText = expiryDate,
            expiryDateValidationResultHandler = expiryDateValidationResultHandler,
            expiryDateSanitiser = ExpiryDateSanitiser()
        )

        val expiryYear = EditText(context)

        expiryYear.addTextChangedListener(expiryYearTextWatcher)

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
