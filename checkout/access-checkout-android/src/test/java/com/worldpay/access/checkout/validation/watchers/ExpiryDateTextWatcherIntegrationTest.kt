package com.worldpay.access.checkout.validation.watchers

import android.widget.EditText
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
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
            expiryDateValidationResultHandler = expiryDateValidationResultHandler
        )

        expiryDate.addTextChangedListener(expiryDateTextWatcher)
    }

    @Test
    fun `should validate expiry date as true where month and year is valid`() {
        enterExpiryDate("04", getYear(1))

        verify(expiryDateValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should validate expiry date as false where year is in the past`() {
        enterExpiryDate("04", getYear(-1))

        verify(expiryDateValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should validate expiry date as false where month is in the past`() {
        enterExpiryDate(getMonth(-1), getYear())

        verify(expiryDateValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should validate expiry date as false where month is invalid and year is invalid`() {
        enterExpiryDate(getMonth(), getYear(-1))

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
            expiryDateValidationResultHandler = expiryDateValidationResultHandler
        )

        val expiryYear = EditText(context)

        expiryYear.addTextChangedListener(expiryYearTextWatcher)

        enterExpiryDate("05", "20")

        verify(expiryDateValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should validate expiry date as false where month is non-numeric`() {
        enterExpiryDate("abc", getYear())

        verify(expiryDateValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should validate expiry date as false where year is non-numeric`() {
        enterExpiryDate(getMonth(), "abc")

        verify(expiryDateValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should not send any validation result where month is not provided`() {
        enterExpiryDate(null, getYear())

        verify(expiryDateValidationResultHandler, never()).handleResult(any())
    }

    @Test
    fun `should not send any validation result where year is not provided`() {
        enterExpiryDate(getMonth(), null)

        verify(expiryDateValidationResultHandler, never()).handleResult(any())
    }

    private fun enterExpiryDate(month: String?, year: String?) {
        if (year != null) expiryDate.setText(year)
    }

    private fun getMonth(offset: Int = 0): String {
        if (offset == 0) return LocalDate.now().month.value.toString()

        if (offset < 0) return LocalDate.now().minusMonths(abs(offset).toLong()).monthValue.toString()

        return LocalDate.now().plusMonths(offset.toLong()).monthValue.toString()
    }

    private fun getYear(offset: Int = 0): String {
        val currentYear = Year.now().value
        return (currentYear + offset).toString().drop(2)
    }

}
