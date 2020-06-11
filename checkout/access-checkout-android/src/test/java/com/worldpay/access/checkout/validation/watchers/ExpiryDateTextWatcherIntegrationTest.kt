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
            expiryDateValidationResultHandler = expiryDateValidationResultHandler
        )

        expiryDate.addTextChangedListener(expiryDateTextWatcher)
    }

    @Test
    fun `should append forward slash after month is entered`() {
        expiryDate.setText("02")

        assertEquals("02/", expiryDate.text.toString())
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
            expiryDateValidationResultHandler = expiryDateValidationResultHandler
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
