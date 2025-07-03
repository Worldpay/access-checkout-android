package com.worldpay.access.checkout.client.validation.pan

import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.testutils.CardNumberUtil.MASTERCARD_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.MASTERCARD_PAN_FORMATTED
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PanFilteringIntegrationTest : AbstractValidationIntegrationTest() {

    @Test
    fun `should allow text within limit`() = runTest {
        initialiseValidation(enablePanFormatting = false)

        pan.setTextAndWait("1")
        assertEquals("1", pan.text.toString())

        pan.setTextAndWait(visaPan())
        assertEquals(visaPan(), pan.text.toString())
    }

    @Test
    fun `should limit to max length - formatting disabled`() = runTest {
        initialiseValidation(enablePanFormatting = false)
        val visaPan = visaPan(19)

        pan.setTextAndWait(visaPan.plus("1111111"))

        assertEquals(visaPan, pan.text.toString())
    }

    @Test
    fun `should limit to max length - formatting enabled`() = runTest {
        initialiseValidation(enablePanFormatting = true)
        val visaPan = visaPan(19, true)

        pan.setTextAndWait(visaPan.plus(" 1111 1111 1111"))

        assertEquals(visaPan, pan.text.toString())
    }

    @Test
    fun `should trim and move the cursor to end of pan when pasting over existing pan entirely`() =
        runTest {
            initialiseValidation(enablePanFormatting = true)
            val visaPan = visaPan(formatted = true)

            pan.setTextAndWait(MASTERCARD_PAN_FORMATTED)
            pan.setTextAndWait(visaPan)

            assertEquals(visaPan, pan.text.toString())
            assertEquals(19, pan.selectionEnd)
        }

    @Test
    fun `should strip out non digits and take max number of digits allowed by brand`() = runTest {
        initialiseValidation(enablePanFormatting = true)
        pan.setTextAndWait("4444abc3333def2222ghi1111klm0000nop9999")

        assertEquals("4444 3333 2222 1111 000", pan.text.toString())
        assertEquals(23, pan.selectionEnd)
    }

    @Test
    fun `should strip out non digits and take max number of digits allowed by brand - formatting disabled`() =
        runTest {
            initialiseValidation(enablePanFormatting = false)

            pan.setTextAndWait("4444abc3333def2222ghi1111klm0000nop9999")

            assertEquals("4444333322221111000", pan.text.toString())
            assertEquals(19, pan.selectionEnd)
        }

    @Test
    fun `should change the max length depending on the pan detected - formatting disabled`() =
        runTest {
            initialiseValidation(enablePanFormatting = false)
            val visaPan = visaPan(19)

            pan.setTextAndWait(visaPan.plus("123"))

            assertEquals(visaPan, pan.text.toString())

            pan.setTextAndWait(MASTERCARD_PAN.plus("1234"))
            assertEquals(MASTERCARD_PAN, pan.text.toString())
        }

    @Test
    fun `should change the max length depending on the pan detected - formatting enabled`() =
        runTest {
            initialiseValidation(enablePanFormatting = true)
            val visaPan = visaPan(19, true)

            pan.setTextAndWait(visaPan.plus(" 5678 90"))

            assertEquals(visaPan, pan.text.toString())

            pan.setTextAndWait(MASTERCARD_PAN_FORMATTED.plus(" 3456 7890"))
            assertEquals(MASTERCARD_PAN_FORMATTED, pan.text.toString())
        }

    @Test
    fun `should trim digits at the end of pan when entering a digit in middle of pan that has reached max length`() =
        runTest {
            initialiseValidation(enablePanFormatting = true)
            val visaPan = visaPan(19, true)

            pan.setTextAndWait(visaPan)
            assertEquals(visaPan, pan.text.toString())

            pan.typeAtIndex(6, "5")
            assertEquals("4444 3533 3222 2111 100", pan.text.toString())
        }

    @Test
    fun `should not allow typing extra digits at end of pan that has reached max length`() =
        runTest {
            initialiseValidation(enablePanFormatting = true)
            val visaPan = visaPan(19, true)

            pan.setTextAndWait(visaPan)
            assertEquals(visaPan, pan.text.toString())

            pan.typeAtIndex(visaPan.length, "5")
            assertEquals(visaPan, pan.text.toString())
        }
}
