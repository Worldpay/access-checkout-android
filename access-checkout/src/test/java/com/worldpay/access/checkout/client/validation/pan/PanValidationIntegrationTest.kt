package com.worldpay.access.checkout.client.validation.pan

import android.os.Looper.getMainLooper
import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.toCardBrandList
import com.worldpay.access.checkout.testutils.CardNumberUtil.INVALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.PARTIAL_VISA
import com.worldpay.access.checkout.testutils.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import com.worldpay.access.checkout.testutils.waitForQueueUntilIdle
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PanValidationIntegrationTest : AbstractValidationIntegrationTest() {

    @Test
    fun `should validate pan as false when partial unknown pan is entered`() = runBlocking {
        initialiseValidation(enablePanFormatting = false)

        pan.setText("00000")

        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener, never()).onBrandsChange(any())
    }

    @Test
    fun `should validate pan as false when partial visa pan is entered`() = runBlocking {
        initialiseValidation(enablePanFormatting = false)

        pan.setText(PARTIAL_VISA)
        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))
    }

    @Test
    fun `should validate pan as true when full visa pan is entered`() = runBlocking {
        initialiseValidation(enablePanFormatting = false)

        val validVisaPan = visaPan()
        pan.setText(validVisaPan)

        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))
    }

    @Test
    fun `should trim and not revalidate pan when pan is over max length`() = runBlocking {
        initialiseValidation(enablePanFormatting = true)

        pan.setText(visaPan(19, true))
        shadowOf(getMainLooper()).waitForQueueUntilIdle()
        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))
        reset(cardValidationListener)

        pan.typeAtIndex(23, "5")
        verifyNoInteractions(cardValidationListener)
        assertEquals(visaPan(19, true), pan.text.toString())
    }

    @Test
    fun `should validate pan as true when visa pan is entered and visa pan is an accepted card brand`() =
        runBlocking {
            initialiseValidation(enablePanFormatting = false)

            pan.setText(visaPan())
            shadowOf(getMainLooper()).waitForQueueUntilIdle()

            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))
        }

    @Test
    fun `should validate pan as true when unknown valid luhn pan is entered and there are some accepted cards specified`() =
        runBlocking {
            initialiseValidation(enablePanFormatting = false)

            pan.setText(VALID_UNKNOWN_LUHN)

            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener, never()).onBrandsChange(any())
        }

    @Test
    fun `should validate pan as false when visa pan is entered and visa is not an accepted card brand and force notify`() =
        runBlocking {
            initialiseValidation(enablePanFormatting = false)

            pan.setText(visaPan())
            shadowOf(getMainLooper()).waitForQueueUntilIdle()

            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))
        }

    @Test
    fun `should validate pan as true when 19 character unknown valid luhn is entered`() =
        runBlocking {
            initialiseValidation(enablePanFormatting = false)

            pan.setText(VALID_UNKNOWN_LUHN)

            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener, never()).onBrandsChange(any())
        }

    @Test
    fun `should validate pan as true when 19 character unknown invalid luhn is entered`() =
        runBlocking {
            initialiseValidation(enablePanFormatting = false)

            pan.setText(INVALID_UNKNOWN_LUHN)

            verify(cardValidationListener, never()).onPanValidated(any())
            verify(cardValidationListener, never()).onBrandsChange(any())
        }

    @Test
    fun `should not validate cvc when pan brand is recognised and cvc is empty`() = runBlocking {
        initialiseValidation(enablePanFormatting = false)
        pan.setText(visaPan())
        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener, never()).onCvcValidated(any())
        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))
    }
}
