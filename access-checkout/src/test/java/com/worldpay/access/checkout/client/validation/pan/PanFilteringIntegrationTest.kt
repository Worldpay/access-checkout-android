package com.worldpay.access.checkout.client.validation.pan

import android.os.Looper.getMainLooper
import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.testutils.CardNumberUtil.MASTERCARD_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.MASTERCARD_PAN_FORMATTED
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import com.worldpay.access.checkout.testutils.waitForQueueUntilIdle
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import org.robolectric.annotation.LooperMode.Mode.PAUSED

@RunWith(RobolectricTestRunner::class)
@LooperMode(PAUSED)
class PanFilteringIntegrationTest : AbstractValidationIntegrationTest() {

    @Before
    fun setup() {
        initialiseValidation(enablePanFormatting = true)
    }

    @Test
    fun `should allow text within limit`() {
        initialiseValidation(enablePanFormatting = false)

        pan.setText("1")
        assertEquals("1", pan.text.toString())

        pan.setText(visaPan())
        assertEquals(visaPan(), pan.text.toString())
    }

    @Test
    fun `should limit to max length - formatting disabled`() {
        initialiseValidation(enablePanFormatting = false)
        val visaPan = visaPan(19)

        pan.setText(visaPan.plus("1111111"))

        assertEquals(visaPan, pan.text.toString())
    }

    @Test
    fun `should limit to max length - formatting enabled`() {
        val visaPan = visaPan(19, true)

        pan.setText(visaPan.plus(" 1111 1111 1111"))

        assertEquals(visaPan, pan.text.toString())
    }

    @Test
    fun `should trim and move the cursor to end of pan when pasting over existing pan entirely`() {
        val visaPan = visaPan(formatted = true)

        pan.setText(MASTERCARD_PAN_FORMATTED)
        pan.setText(visaPan)

        assertEquals(visaPan, pan.text.toString())
        assertEquals(19, pan.selectionEnd)
    }

    @Test
    fun `should strip out non digits and take max number of digits allowed by brand`() {
        pan.setText("4444abc3333def2222ghi1111klm0000nop9999")

        assertEquals("4444 3333 2222 1111 000", pan.text.toString())
        assertEquals(23, pan.selectionEnd)
    }

    @Test
    fun `should strip out non digits and take max number of digits allowed by brand - formatting disabled`() {
        initialiseValidation(enablePanFormatting = false)

        pan.setText("4444abc3333def2222ghi1111klm0000nop9999")

        assertEquals("4444333322221111000", pan.text.toString())
        assertEquals(19, pan.selectionEnd)
    }

    @Test
    fun `should change the max length depending on the pan detected - formatting disabled`() {
        initialiseValidation(enablePanFormatting = false)
        val visaPan = visaPan(19)

        pan.setText(visaPan.plus("123"))
        shadowOf(getMainLooper()).waitForQueueUntilIdle()
        assertEquals(visaPan, pan.text.toString())

        pan.setText(MASTERCARD_PAN.plus("1234"))
        assertEquals(MASTERCARD_PAN, pan.text.toString())
    }

    @Test
    fun `should change the max length depending on the pan detected - formatting enabled`() {
        val visaPan = visaPan(19, true)

        pan.setText(visaPan.plus(" 5678 90"))
        shadowOf(getMainLooper()).waitForQueueUntilIdle()
        assertEquals(visaPan, pan.text.toString())

        pan.setText(MASTERCARD_PAN_FORMATTED.plus(" 3456 7890"))
        assertEquals(MASTERCARD_PAN_FORMATTED, pan.text.toString())
    }

    @Test
    fun `should trim digits at the end of pan when entering a digit in middle of pan that has reached max length`() {
        val visaPan = visaPan(19, true)

        pan.setText(visaPan)
        assertEquals(visaPan, pan.text.toString())

        pan.typeAtIndex(6, "5")
        assertEquals("4444 3533 3222 2111 100", pan.text.toString())
    }

    @Test
    fun `should not allow typing extra digits at end of pan that has reached max length`() {
        val visaPan = visaPan(19, true)

        pan.setText(visaPan)
        assertEquals(visaPan, pan.text.toString())

        pan.typeAtIndex(visaPan.length, "5")
        assertEquals(visaPan, pan.text.toString())
    }
}
