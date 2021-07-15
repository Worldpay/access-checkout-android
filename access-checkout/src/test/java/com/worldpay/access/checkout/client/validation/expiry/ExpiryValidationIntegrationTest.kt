package com.worldpay.access.checkout.client.validation.expiry

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowSystemClock
import java.lang.System.currentTimeMillis
import java.time.Duration
import java.time.Instant.ofEpochMilli
import java.time.LocalDate
import java.time.Year
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
class ExpiryValidationIntegrationTest : AbstractValidationIntegrationTest() {

    @Before
    fun setup() {
        initialiseValidation()
    }

    @Test
    fun `should validate expiry date as true where month and year is valid`() {
        expiryDate.setText("04/${getYear(1)}")

        verify(cardValidationListener).onExpiryDateValidated(true)
    }

    @Test
    fun `should never call validation listener where year is in the past`() {
        expiryDate.setText("04/${getYear(-1)}")

        verify(cardValidationListener, never()).onExpiryDateValidated(any())
    }

    @Test
    fun `should never call validation listener where month is in the past`() {
        val monthInPast = getMonth(-1)

        if (monthInPast == "12") {
            // in the month of january, -1 on the year as well
            expiryDate.setText("$monthInPast/${getYear(-1)}")
        } else {
            expiryDate.setText("$monthInPast/${getYear()}")
        }

        verify(cardValidationListener, never()).onExpiryDateValidated(any())
    }

    @Test
    fun `should never call validation listener where month is invalid and year is invalid`() {
        expiryDate.setText("05/19")

        verify(cardValidationListener, never()).onExpiryDateValidated(any())
    }

    @Test
    fun `should validate expiry date as true where month and year is valid and current day is last day of month`() {
        val future = 2222028000000 // 23:00:00 UTC May 30 2040
        ShadowSystemClock.advanceBy(Duration.between(
            ofEpochMilli(currentTimeMillis()),
            ofEpochMilli(future)
        ))

        expiryDate.setText("06/40")

        verify(cardValidationListener).onExpiryDateValidated(true)
    }

    @Test
    fun `should never call validation listener where month is non-numeric`() {
        expiryDate.setText("ab/${getYear()}")

        verify(cardValidationListener, never()).onExpiryDateValidated(any())
    }

    @Test
    fun `should never call validation listener where year is non-numeric`() {
        expiryDate.setText("${getMonth()}/ab")

        verify(cardValidationListener, never()).onExpiryDateValidated(any())
    }

    @Test
    fun `should not send any validation result where month is not provided`() {
        expiryDate.setText(getYear())

        verify(cardValidationListener, never()).onExpiryDateValidated(any())
    }

    @Test
    fun `should not send any validation result where year is not provided`() {
        expiryDate.setText(getMonth())

        verify(cardValidationListener, never()).onExpiryDateValidated(any())
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
