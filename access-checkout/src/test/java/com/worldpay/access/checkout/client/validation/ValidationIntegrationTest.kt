package com.worldpay.access.checkout.client.validation

import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.AMEX_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.toCardBrand
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.reset
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class ValidationIntegrationTest : AbstractValidationIntegrationTest() {

    private val visaCard = toCardBrand(VISA_BRAND)

    @Before
    fun setup() = runTest {
        initialiseValidation()
        reset(cardValidationListener)
        advanceUntilIdle()
    }

    @Test
    fun `should call each listener function as each input is filled and then finally call the onValidationSuccess function`() =
        runTest {
            setText(pan, visaPan())
            verify(cardValidationListener, timeout(1000)).onPanValidated(true)
            verify(cardValidationListener, timeout(1000)).onBrandsChange(listOf(visaCard))

            setText(cvc, "1234")
            verify(cardValidationListener).onCvcValidated(true)

            setText(expiryDate, "1229")
            verify(cardValidationListener).onExpiryDateValidated(true)
            verify(cardValidationListener).onValidationSuccess()
        }

    @Test
    fun `should revalidate cvc when brand changes`() = runTest {
        val amexCardBrand = toCardBrand(AMEX_BRAND)
        setText(cvc, "123")
        verify(cardValidationListener).onCvcValidated(true)

        setText(pan, AMEX_PAN)
        verify(cardValidationListener).onCvcValidated(false)
        verify(cardValidationListener).onBrandsChange(listOf(amexCardBrand))

        reset(cardValidationListener)
        setText(pan, visaPan())
        verify(cardValidationListener).onBrandsChange(listOf(toCardBrand(VISA_BRAND)))
        verify(cardValidationListener).onCvcValidated(true)

    }
}
