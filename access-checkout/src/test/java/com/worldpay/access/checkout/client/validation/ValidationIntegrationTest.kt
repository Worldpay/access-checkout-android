package com.worldpay.access.checkout.client.validation

import android.os.Looper.getMainLooper
import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.client.validation.model.CardBrand
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.AMEX_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.MASTERCARD_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.toCardBrand
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import com.worldpay.access.checkout.testutils.waitForQueueUntilIdle
import kotlin.test.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class ValidationIntegrationTest : AbstractValidationIntegrationTest() {

    private val masterCardBrand = toCardBrand(MASTERCARD_BRAND)

    @Before
    fun setup() {
        initialiseValidation()
    }

    @Test
    fun `should call each listener function as each input is filled and then finally call the onValidationSuccess function`() {
        pan.setText(visaPan())
        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandsChange(listOf(toCardBrand(VISA_BRAND),masterCardBrand))

        cvc.setText("1234")
        verify(cardValidationListener).onCvcValidated(true)

        expiryDate.setText("1229")
        verify(cardValidationListener).onExpiryDateValidated(true)

        verify(cardValidationListener).onValidationSuccess()
    }

    @Test
    fun `should revalidate cvc when brand changes`() {
        val amexCardBrand = toCardBrand(AMEX_BRAND)

        cvc.setText("123")

        verify(cardValidationListener).onCvcValidated(true)

        pan.setText(AMEX_PAN)
        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        verify(cardValidationListener).onCvcValidated(false)
        verify(cardValidationListener).onBrandsChange(listOf(amexCardBrand,masterCardBrand))

        reset(cardValidationListener)

        pan.setText(visaPan())
        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        // We assert that the brand has changed this way and not using verify()
        // because verify(visaPan()) does not work consistently in our BitRise builds
        val brandsArgCaptor = argumentCaptor<List<CardBrand>>()
        verify(cardValidationListener).onBrandsChange(brandsArgCaptor.capture())
        val capturedBrands = brandsArgCaptor.firstValue
        assertNotEquals(amexCardBrand, capturedBrands[0])

        verify(cardValidationListener).onCvcValidated(true)
    }
}
