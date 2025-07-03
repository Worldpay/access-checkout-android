package com.worldpay.access.checkout.client.validation

import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.client.validation.model.CardBrand
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.AMEX_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.toCardBrand
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertNotEquals

@RunWith(RobolectricTestRunner::class)
class ValidationIntegrationTest : AbstractValidationIntegrationTest() {

    private val visaCard = toCardBrand(VISA_BRAND)

    @Before
    fun setup() {
        initialiseValidation()
        reset(cardValidationListener)
    }

    @Test
    fun `should call each listener function as each input is filled and then finally call the onValidationSuccess function`() =
        runTest {
            pan.setTextAndWait(visaPan())

            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener).onBrandsChange(listOf(visaCard))

            cvc.setTextAndWait("1234")

            verify(cardValidationListener).onCvcValidated(true)

            expiryDate.setTextAndWait("1229")

            verify(cardValidationListener).onExpiryDateValidated(true)


            verify(cardValidationListener).onValidationSuccess()
        }

    @Test
    fun `should revalidate cvc when brand changes`() = runTest {
        val amexCardBrand = toCardBrand(AMEX_BRAND)

        cvc.setTextAndWait("123")

        verify(cardValidationListener).onCvcValidated(true)


        pan.setTextAndWait(AMEX_PAN)
        verify(cardValidationListener).onCvcValidated(false)
        verify(cardValidationListener).onBrandsChange(listOf(amexCardBrand))

        reset(cardValidationListener)

        pan.setTextAndWait(visaPan())

        // We assert that the brand has changed this way and not using verify()
        // because verify(visaPan()) does not work consistently in our BitRise builds
        val brandsArgCaptor = argumentCaptor<List<CardBrand>>()
        verify(cardValidationListener).onBrandsChange(brandsArgCaptor.capture())
        val capturedBrands = brandsArgCaptor.firstValue
        assertNotEquals(amexCardBrand, capturedBrands[0])

        verify(cardValidationListener).onCvcValidated(true)

    }
}
