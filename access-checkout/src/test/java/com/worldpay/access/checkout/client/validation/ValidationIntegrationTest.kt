package com.worldpay.access.checkout.client.validation

import android.os.Looper.getMainLooper
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.AMEX_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.toCardBrand
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import com.worldpay.access.checkout.testutils.waitForQueueUntilIdle
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class ValidationIntegrationTest : AbstractValidationIntegrationTest() {

    @Before
    fun setup() {
        initialiseValidation()
    }

    @Test
    fun `should call each listener function as each input is filled and then finally call the onValidationSuccess function`() {
        pan.setText(visaPan())
        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandChange(toCardBrand(VISA_BRAND))

        cvc.setText("1234")
        verify(cardValidationListener).onCvcValidated(true)

        expiryDate.setText("1229")
        verify(cardValidationListener).onExpiryDateValidated(true)

        verify(cardValidationListener).onValidationSuccess()
    }

    @Test
    fun `should revalidate cvc when brand changes`() {
        cvc.setText("123")

        verify(cardValidationListener).onCvcValidated(true)

        pan.setText(AMEX_PAN)
        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener).onCvcValidated(false)
        verify(cardValidationListener).onBrandChange(toCardBrand(AMEX_BRAND))

        reset(cardValidationListener)
        pan.setText(visaPan())

        verify(cardValidationListener).onCvcValidated(true)
        verify(cardValidationListener).onBrandChange(toCardBrand(VISA_BRAND))
    }
}
