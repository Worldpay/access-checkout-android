package com.worldpay.access.checkout.client.validation.pan

import android.os.Looper.getMainLooper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.AMEX_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.DINERS_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.DISCOVER_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.JCB_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.MAESTRO_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.MASTERCARD_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.toCardBrand
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.DINERS_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.DISCOVER_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.INVALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.JCB_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.MAESTRO_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.MASTERCARD_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.PARTIAL_VISA
import com.worldpay.access.checkout.testutils.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import com.worldpay.access.checkout.testutils.waitForQueueUntilIdle
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class PanAndCardBrandValidationIntegrationTest : AbstractValidationIntegrationTest() {

    @Before
    fun setup() {
        initialiseValidation()
    }

    @Test
    fun `should accept an unknown valid luhn pan when no accepted cards have been specified`() {
        pan.setText(VALID_UNKNOWN_LUHN)

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener, never()).onBrandChange(any())
    }

    @Test
    fun `should accept an unknown valid luhn pan when all cards are accepted`() {
        initialiseValidation(
            acceptedCardBrands = arrayOf(
                "AMEX",
                "DINERS",
                "DISCOVER",
                "JCB",
                "MAESTRO",
                "MASTERCARD",
                "VISA"
            )
        )

        pan.setText(VALID_UNKNOWN_LUHN)

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener, never()).onBrandChange(any())
    }

    @Test
    fun `should not call listener at all when pan is complete but invalid and unrecognised`() {
        pan.setText(INVALID_UNKNOWN_LUHN)
        verifyZeroInteractions(cardValidationListener)
    }

    @Test
    fun `should not call listener at all when pan is partial but invalid and unrecognised`() {
        pan.setText("000")
        verifyZeroInteractions(cardValidationListener)
    }

    @Test
    fun `should not validate pan for partial visa pan but should call brand changed with visa brand`() {
        pan.setText(PARTIAL_VISA)

        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(toCardBrand(VISA_BRAND))
    }

    @Test
    fun `should only notify pan validated on validation state change and notify brand change each time the brand changes - without accepted card brands`() {
        pan.setText(visaPan())

        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandChange(toCardBrand(VISA_BRAND))

        reset(cardValidationListener)

        pan.setText(MASTERCARD_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(toCardBrand(MASTERCARD_BRAND))

        reset(cardValidationListener)

        pan.setText(AMEX_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(toCardBrand(AMEX_BRAND))

        reset(cardValidationListener)

        pan.setText(JCB_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(toCardBrand(JCB_BRAND))

        reset(cardValidationListener)

        pan.setText(DISCOVER_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(toCardBrand(DISCOVER_BRAND))

        reset(cardValidationListener)

        pan.setText(DINERS_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(toCardBrand(DINERS_BRAND))

        reset(cardValidationListener)

        pan.setText(MAESTRO_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(toCardBrand(MAESTRO_BRAND))

        reset(cardValidationListener)

        pan.setText("")
        verify(cardValidationListener).onPanValidated(false)
        verify(cardValidationListener).onBrandChange(null)
    }

    @Test
    fun `should only notify pan validated on validation state change and notify brand change each time the brand changes - with empty array of accepted card brands`() {
        initialiseValidation(acceptedCardBrands = emptyArray())

        pan.setText(visaPan())

        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandChange(toCardBrand(VISA_BRAND))

        reset(cardValidationListener)

        pan.setText(MASTERCARD_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(toCardBrand(MASTERCARD_BRAND))

        reset(cardValidationListener)

        pan.setText(AMEX_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(toCardBrand(AMEX_BRAND))

        reset(cardValidationListener)

        pan.setText(JCB_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(toCardBrand(JCB_BRAND))

        reset(cardValidationListener)

        pan.setText(DISCOVER_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(toCardBrand(DISCOVER_BRAND))

        reset(cardValidationListener)

        pan.setText(DINERS_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(toCardBrand(DINERS_BRAND))

        reset(cardValidationListener)

        pan.setText(MAESTRO_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(toCardBrand(MAESTRO_BRAND))

        reset(cardValidationListener)

        pan.setText("")
        verify(cardValidationListener).onPanValidated(false)
        verify(cardValidationListener).onBrandChange(null)
    }

    @Test
    fun `should invalidate the cvc after the pan has been validated with a brand and the cvc is now incorrect`() {
        cvc.setText("1234")
        verify(cardValidationListener).onCvcValidated(true)

        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        pan.setText(visaPan())
        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandChange(toCardBrand(VISA_BRAND))
        verify(cardValidationListener).onCvcValidated(false)
    }

    @Test
    fun `should accept amex card when amex is the only accepted card brand`() {
        initialiseValidation(acceptedCardBrands = arrayOf("AMEX"))

        pan.setText(AMEX_PAN)

        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandChange(toCardBrand(AMEX_BRAND))
    }
// TODO: US707277 fix this test
    @Test
    fun `should not accept diners card when diners is not accepted`() {
        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        initialiseValidation(acceptedCardBrands = arrayOf("AMEX"))

        pan.setText(DINERS_PAN)

        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener).onPanValidated(false)
        verify(cardValidationListener).onBrandChange(toCardBrand(DINERS_BRAND))
    }
}
