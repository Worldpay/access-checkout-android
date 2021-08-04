package com.worldpay.access.checkout.client.validation.cvc

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CvcValidationIntegrationTest : AbstractValidationIntegrationTest() {

    @Before
    fun setup() {
        initialiseValidation()
    }

    @Test
    fun `should never call validation listener given 1 digit cvc is entered and no pan`() {
        cvc.setText("1")

        verify(cardValidationListener, never()).onCvcValidated(any())
    }

    @Test
    fun `should never call validation listener given 2 digit cvc is entered and no pan`() {
        cvc.setText("12")

        verify(cardValidationListener, never()).onCvcValidated(any())
    }

    @Test
    fun `should validate cvc as true given 3 digit cvc is entered and no pan`() {
        cvc.setText("123")

        verify(cardValidationListener).onCvcValidated(true)
    }

    @Test
    fun `should validate cvc as true given 4 digit cvc is entered and no pan`() {
        cvc.setText("1234")

        verify(cardValidationListener).onCvcValidated(true)
    }

    @Test
    fun `should limit to 4 digits and validate cvc as true given no pan is entered`() {
        cvc.setText("12345")

        verify(cardValidationListener).onCvcValidated(true)
        assertEquals("1234", cvc.text.toString())
        assertEquals("", pan.text.toString())
    }

    @Test
    fun `should validate cvc as true given 3 digit cvc is entered and visa pan is entered`() {
        pan.setText(visaPan())
        cvc.setText("123")

        verify(cardValidationListener).onCvcValidated(true)
    }

    @Test
    fun `should limit cvc to 3 digits and validate cvc as true given 4 digit cvc is entered and visa pan is entered`() {
        pan.setText(visaPan())
        cvc.setText("1234")

        verify(cardValidationListener).onCvcValidated(true)
        assertEquals("123", cvc.text.toString())
    }

    @Test
    fun `should validate cvc as true given 4 digit cvc is entered and amex pan is entered`() {
        pan.setText(AMEX_PAN)
        cvc.setText("1234")

        verify(cardValidationListener).onCvcValidated(true)
    }

    @Test
    fun `should limit cvc to 3 digits and validate cvc as true given 4 digit cvc is entered and amex pan is entered`() {
        pan.setText(AMEX_PAN)
        cvc.setText("12345")

        verify(cardValidationListener).onCvcValidated(true)
        assertEquals("1234", cvc.text.toString())
    }
}
