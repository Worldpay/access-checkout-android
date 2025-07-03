package com.worldpay.access.checkout.client.validation.pan

import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.toCardBrandList
import com.worldpay.access.checkout.testutils.CardNumberUtil.INVALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.PARTIAL_VISA
import com.worldpay.access.checkout.testutils.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PanValidationIntegrationTest : AbstractValidationIntegrationTest() {

    @Before
    fun setUp() {
        reset(cardValidationListener)
    }

    @Test
    fun `should validate pan as false when partial unknown pan is entered`() = runTest {
        initialiseValidation(enablePanFormatting = false)

        pan.setTextAndWait("00000")

        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener, never()).onBrandsChange(any())
    }

    @Test
    fun `should validate pan as false when partial visa pan is entered`() = runTest {
        initialiseValidation(enablePanFormatting = false)

        pan.setTextAndWait(PARTIAL_VISA)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))
    }

    @Test
    fun `should validate pan as true when full visa pan is entered`() = runTest {
        initialiseValidation(enablePanFormatting = false)

        val validVisaPan = visaPan()

        pan.setTextAndWait(validVisaPan)

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))
    }

    @Test
    fun `should trim and not revalidate pan when pan is over max length`() = runTest {
        initialiseValidation(enablePanFormatting = true)

        pan.setTextAndWait(visaPan(19, true))

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))
        reset(cardValidationListener)

        pan.typeAtIndex(23, "5")
        verifyNoInteractions(cardValidationListener) // Ensure no interactions with the listener
        assertEquals(visaPan(19, true), pan.text.toString())
    }

    @Test
    fun `should validate pan as true when visa pan is entered and visa pan is an accepted card brand`() =
        runTest {
            initialiseValidation(enablePanFormatting = false)

            pan.setTextAndWait(visaPan())

            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))
        }

    @Test
    fun `should validate pan as true when unknown valid luhn pan is entered and there are some accepted cards specified`() =
        runTest {
            initialiseValidation(enablePanFormatting = false)

            pan.setTextAndWait(VALID_UNKNOWN_LUHN)

            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener, never()).onBrandsChange(any())
        }

    @Test
    fun `should validate pan as false when visa pan is entered and visa is not an accepted card brand and force notify`() =
        runTest {
            initialiseValidation(enablePanFormatting = false)

            pan.setTextAndWait(visaPan())

            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))
        }

    @Test
    fun `should validate pan as true when 19 character unknown valid luhn is entered`() =
        runTest {
            initialiseValidation(enablePanFormatting = false)

            pan.setTextAndWait(VALID_UNKNOWN_LUHN)

            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener, never()).onBrandsChange(any())
        }

    @Test
    fun `should validate pan as true when 19 character unknown invalid luhn is entered`() =
        runTest {
            initialiseValidation(enablePanFormatting = false)

            pan.setTextAndWait(INVALID_UNKNOWN_LUHN)

            verify(cardValidationListener, never()).onPanValidated(any())
            verify(cardValidationListener, never()).onBrandsChange(any())
        }

    @Test
    fun `should not validate cvc when pan brand is recognised and cvc is empty`() = runTest {
        initialiseValidation(enablePanFormatting = false)

        pan.setTextAndWait(visaPan())

        verify(cardValidationListener, never()).onCvcValidated(any())
        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))
    }
}
