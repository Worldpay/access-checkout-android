package com.worldpay.access.checkout.client.validation.pan

import android.os.Looper.getMainLooper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.toCardBrand
import com.worldpay.access.checkout.testutils.CardNumberUtil.INVALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.PARTIAL_VISA
import com.worldpay.access.checkout.testutils.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import com.worldpay.access.checkout.testutils.waitForQueueUntilIdle
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class PanValidationIntegrationTest : AbstractValidationIntegrationTest() {

    @Before
    fun setUp() {
        initialiseValidation(enablePanFormatting = false)
    }

    @Test
    fun `should validate pan as false when partial unknown pan is entered`() {
        pan.setText("00000")

        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener, never()).onBrandChange(any())
    }

    @Test
    fun `should validate pan as false when partial visa pan is entered`() {
        pan.setText(PARTIAL_VISA)
        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(toCardBrand(VISA_BRAND))
    }

    @Test
    fun `should validate pan as true when full visa pan is entered`() {
        pan.setText(visaPan())
        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandChange(toCardBrand(VISA_BRAND))
    }

    @Test
    fun `should trim and not revalidate pan when pan is over max length`() {
        initialiseValidation(enablePanFormatting = true)

        pan.setText(visaPan(19, true))
        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandChange(toCardBrand(VISA_BRAND))

        reset(cardValidationListener)

        pan.typeAtIndex(23, "5")

        verifyZeroInteractions(cardValidationListener)
        assertEquals(visaPan(19, true), pan.text.toString())
    }

    @Test
    fun `should validate pan as true when visa pan is entered and visa pan is an accepted card brand`() {
        pan.setText(visaPan())
        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandChange(toCardBrand(VISA_BRAND))
    }

    @Test
    fun `should validate pan as true when unknown valid luhn pan is entered and there are some accepted cards specified`() {
        pan.setText(VALID_UNKNOWN_LUHN)

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener, never()).onBrandChange(any())
    }

    @Test
    fun `should validate pan as false when visa pan is entered and visa is not an accepted card brand and force notify`() {
        pan.setText(visaPan())
        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandChange(toCardBrand(VISA_BRAND))
    }

    @Test
    fun `should validate pan as true when 19 character unknown valid luhn is entered`() {
        pan.setText(VALID_UNKNOWN_LUHN)

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener, never()).onBrandChange(any())
    }

    @Test
    fun `should validate pan as true when 19 character unknown invalid luhn is entered`() {
        pan.setText(INVALID_UNKNOWN_LUHN)

        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener, never()).onBrandChange(any())
    }

    @Test
    fun `should not validate cvc when pan brand is recognised and cvc is empty`() {
        pan.setText(visaPan())
        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener, never()).onCvcValidated(any())
        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandChange(toCardBrand(VISA_BRAND))
    }
}
