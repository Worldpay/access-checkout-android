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
import org.mockito.kotlin.timeout
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
        setText(pan, "00000")
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener, never()).onCardBrandsChanged(any())
    }

    @Test
    fun `should validate pan as false when partial visa pan is entered`() = runTest {
        initialiseValidation(enablePanFormatting = false)
        setText(pan, PARTIAL_VISA)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener, timeout(500)).onCardBrandsChanged(toCardBrandList(VISA_BRAND))
    }

    @Test
    fun `should validate pan as true when full visa pan is entered`() = runTest {
        initialiseValidation(enablePanFormatting = false)
        val validVisaPan = visaPan()
        setText(pan, validVisaPan)
        verify(cardValidationListener, timeout(500)).onPanValidated(true)
        verify(cardValidationListener, timeout(500)).onCardBrandsChanged(toCardBrandList(VISA_BRAND))
    }

    @Test
    fun `should trim and not revalidate pan when pan is over max length`() = runTest {
        initialiseValidation(enablePanFormatting = true)
        setText(pan, visaPan(19, true))
        verify(
            cardValidationListener,
            timeout(500)
        ).onCardBrandsChanged(toCardBrandList(VISA_BRAND))
        verify(cardValidationListener, timeout(500)).onPanValidated(true)


        reset(cardValidationListener)
        typeAtIndex(pan, 23, "5")
        verifyNoInteractions(cardValidationListener) // Ensure no interactions with the listener
        assertEquals(visaPan(19, true), pan.text.toString())
    }

    @Test
    fun `should validate pan as true when visa pan is entered and visa pan is an accepted card brand`() =
        runTest {
            initialiseValidation(enablePanFormatting = false)
            setText(pan, visaPan())
            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener, timeout(500)).onCardBrandsChanged(
                toCardBrandList(
                    VISA_BRAND
                )
            )
        }

    @Test
    fun `should validate pan as true when unknown valid luhn pan is entered and there are some accepted cards specified`() =
        runTest {
            initialiseValidation(enablePanFormatting = false)
            setText(pan, VALID_UNKNOWN_LUHN)
            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener, never()).onCardBrandsChanged(any())
        }

    @Test
    fun `should validate pan as false when visa pan is entered and visa is not an accepted card brand and force notify`() =
        runTest {
            initialiseValidation(enablePanFormatting = false)
            setText(pan, visaPan())
            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener, timeout(500)).onCardBrandsChanged(
                toCardBrandList(
                    VISA_BRAND
                )
            )
        }

    @Test
    fun `should validate pan as true when 19 character unknown valid luhn is entered`() =
        runTest {
            initialiseValidation(enablePanFormatting = false)

            setText(pan, VALID_UNKNOWN_LUHN)

            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener, never()).onCardBrandsChanged(any())
        }

    @Test
    fun `should validate pan as true when 19 character unknown invalid luhn is entered`() =
        runTest {
            initialiseValidation(enablePanFormatting = false)
            setText(pan, INVALID_UNKNOWN_LUHN)
            verify(cardValidationListener, never()).onPanValidated(any())
            verify(cardValidationListener, never()).onCardBrandsChanged(any())
        }

    @Test
    fun `should not validate cvc when pan brand is recognised and cvc is empty`() = runTest {
        initialiseValidation(enablePanFormatting = false)
        setText(pan, visaPan())
        verify(cardValidationListener, never()).onCvcValidated(any())
        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener, timeout(500)).onCardBrandsChanged(toCardBrandList(VISA_BRAND))
    }
}
