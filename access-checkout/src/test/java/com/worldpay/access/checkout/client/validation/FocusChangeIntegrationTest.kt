package com.worldpay.access.checkout.client.validation

import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.toCardBrandList
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner
import kotlin.test.fail

@RunWith(RobolectricTestRunner::class)
class FocusChangeIntegrationTest : AbstractValidationIntegrationTest() {

    @Before
    fun setup() {
        initialiseValidation()
    }

    @Test
    fun `should not notify validation result on focus lost where notification has already been sent - pan`() =
        runBlocking {
            // Set the PAN and wait for any asynchronous operations to complete
            pan.setTextAndWait(visaPan())

            // Verify the expected interactions
            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))

            pan.requestFocus()
            if (pan.hasFocus()) {
                pan.clearFocus()
            } else {
                fail("could not gain focus")
            }

            verifyNoMoreInteractions(cardValidationListener)
        }

    @Test
    fun `should not notify validation result on focus lost where notification has already been sent - cvc`() =
        runBlocking {
            cvc.setTextAndWait("123")
            verify(cardValidationListener).onCvcValidated(true)

            cvc.requestFocus()

            if (cvc.hasFocus()) {
                cvc.clearFocus()
            } else {
                fail("could not gain focus")
            }

            verifyNoMoreInteractions(cardValidationListener)
        }

    @Test
    fun `should not notify validation result on focus lost where notification has already been sent - expiry date`() =
        runBlocking {
            expiryDate.setTextAndWait("12/99")
            verify(cardValidationListener).onExpiryDateValidated(true)

            expiryDate.requestFocus()

            if (expiryDate.hasFocus()) {
                expiryDate.clearFocus()
            } else {
                fail("could not gain focus")
            }

            verifyNoMoreInteractions(cardValidationListener)
        }

    @Test
    fun `should notify validation result on focus lost where notification has not already been sent - pan`() =
        runBlocking {
            pan.setTextAndWait("0000")
            verifyNoInteractions(cardValidationListener)

            pan.requestFocus()

            if (pan.hasFocus()) {
                pan.clearFocus()
            } else {
                fail("could not gain focus")
            }

            verify(cardValidationListener).onPanValidated(false)
        }

    @Test
    fun `should notify validation result on focus lost where notification has not already been sent - cvc`() =
        runBlocking {
            cvc.setTextAndWait("")
            verifyNoInteractions(cardValidationListener)

            cvc.requestFocus()

            if (cvc.hasFocus()) {
                cvc.clearFocus()
            } else {
                fail("could not gain focus")
            }

            verify(cardValidationListener).onCvcValidated(false)
        }

    @Test
    fun `should notify validation result on focus lost where notification has not already been sent - expiry date`() =
        runBlocking {
            expiryDate.setTextAndWait("01/19")
            verifyNoInteractions(cardValidationListener)

            expiryDate.requestFocus()

            if (expiryDate.hasFocus()) {
                expiryDate.clearFocus()
            } else {
                fail("could not gain focus")
            }

            verify(cardValidationListener).onExpiryDateValidated(false)
        }
}
