package com.worldpay.access.checkout.client.validation.cvc

import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CvcValidationIntegrationTest : AbstractValidationIntegrationTest() {

    @Before
    fun setup() = runTest {
        initialiseValidation()
        advanceUntilIdle()
    }

    @Test
    fun `should never call validation listener given 1 digit cvc is entered and no pan`() =
        runTest {
            setText(cvc, "1")

            verify(cardValidationListener, never()).onCvcValidated(any())
        }

    @Test
    fun `should never call validation listener given 2 digit cvc is entered and no pan`() =
        runTest {
            setText(cvc, "12")

            verify(cardValidationListener, never()).onCvcValidated(any())
        }

    @Test
    fun `should validate cvc as true given 3 digit cvc is entered and no pan`() = runTest {
        setText(cvc, "123")

        verify(cardValidationListener).onCvcValidated(true)
    }

    @Test
    fun `should validate cvc as true given 4 digit cvc is entered and no pan`() = runTest {
        setText(cvc, "1234")

        verify(cardValidationListener).onCvcValidated(true)
    }

    @Test
    fun `should limit to 4 digits and validate cvc as true given no pan is entered`() = runTest {
        setText(cvc, "12345")

        verify(cardValidationListener).onCvcValidated(true)
        assertEquals("1234", cvc.text.toString())
        assertEquals("", pan.text.toString())
    }

    @Test
    fun `should validate cvc as true given 3 digit cvc is entered and visa pan is entered`() =
        runTest {
            pan.setText(visaPan())
            setText(cvc, "123")

            verify(cardValidationListener).onCvcValidated(true)
        }

    @Test
    fun `should limit cvc to 3 digits and validate cvc as true given 4 digit cvc is entered and visa pan is entered`() =
        runTest {
            pan.setText(visaPan())
            setText(cvc, "1234")

            verify(cardValidationListener).onCvcValidated(true)
            assertEquals("123", cvc.text.toString())
        }

    @Test
    fun `should validate cvc as true given 4 digit cvc is entered and amex pan is entered`() =
        runTest {
            pan.setText(AMEX_PAN)
            setText(cvc, "1234")

            verify(cardValidationListener).onCvcValidated(true)
        }

    @Test
    fun `should limit cvc to 3 digits and validate cvc as true given 4 digit cvc is entered and amex pan is entered`() =
        runTest {
            pan.setText(AMEX_PAN)
            setText(cvc, "12345")

            verify(cardValidationListener).onCvcValidated(true)
            assertEquals("1234", cvc.text.toString())
        }
}
