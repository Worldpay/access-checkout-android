package com.worldpay.access.checkout.client.validation.pan

import android.os.Looper.getMainLooper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.AMEX_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.toCardBrand
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN_FORMATTED
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import com.worldpay.access.checkout.testutils.waitForQueueUntilIdle
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class PanFormattingIntegrationTest : AbstractValidationIntegrationTest() {

    @Before
    fun setup() {
        initialiseValidation(enablePanFormatting = true)
    }

    @Test
    fun `should be formatting pan with a space between every 4 digits and detecting correct brand - visa`() {
        pan.setText("4111111111111111")

        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        assertEquals("4111 1111 1111 1111", pan.text.toString())
        verify(cardValidationListener).onBrandChange(toCardBrand(VISA_BRAND))
        verify(cardValidationListener).onPanValidated(true)
    }

    @Test
    fun `should be formatting pan with a space between after 4, 6 and 5 digits and detected correct brand`() {
        shadowOf(getMainLooper()).waitForQueueUntilIdle(1)

        pan.setText("342793178931249")

        assertEquals("3427 931789 31249", pan.text.toString())
        verify(cardValidationListener).onBrandChange(toCardBrand(AMEX_BRAND))
        verify(cardValidationListener).onPanValidated(true)
    }

    @Test
    fun `should not be formatting pan when formatting is disabled`() {
        initialiseValidation(enablePanFormatting = false)

        pan.setText("4111111111111111")

        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        assertEquals("4111111111111111", pan.text.toString())
        verify(cardValidationListener).onBrandChange(toCardBrand(VISA_BRAND))
        verify(cardValidationListener).onPanValidated(true)
    }

    @Test
    fun `should delete character before space when deleting space in pan`() {
        pan.setText("1234 5678 9012")
        pan.pressBackspaceAtIndex(5)

        assertEquals("1235 6789 012", pan.text.toString())
        assertEquals(3, pan.selectionEnd)
    }

    @Test
    fun `should delete previous digit when selecting space and pressing backspace`() {
        pan.setText("1234 5678 90")
        pan.pressBackspaceAtSelection(4, 5)

        assertEquals("1235 6789 0", pan.text.toString())
        assertEquals(3, pan.selectionEnd)
    }

    @Test
    fun `should not delete anymore characters when deleting space and a character in pan`() {
        pan.setText("1234 5678 9012")
        pan.pressBackspaceAtSelection(4, 6)

        assertEquals("1234 6789 012", pan.text.toString())
        assertEquals(4, pan.selectionEnd)
    }

    @Test
    fun `should be able to delete character that is not a space in pan`() {
        pan.setText("1234 5678 9012")
        pan.pressBackspaceAtIndex(2)

        assertEquals("1345 6789 012", pan.text.toString())
        assertEquals(1, pan.selectionEnd)
    }

    @Test
    fun `should be able to delete digit, space and a digit together`() {
        pan.setText("1234 5678 90")
        pan.pressBackspaceAtSelection(3, 6)

        assertEquals("1236 7890", pan.text.toString())
        assertEquals(3, pan.selectionEnd)
    }

    @Test
    fun `should be able to delete space and a digit together`() {
        pan.setText("1234 5678 90")
        pan.pressBackspaceAtSelection(4, 6)

        assertEquals("1234 6789 0", pan.text.toString())
        assertEquals(4, pan.selectionEnd)
    }

    @Test
    fun `should update the pan and set cursor position when formatted`() {
        val visaPan = visaPan(formatted = true)

        pan.setText(visaPan())

        assertEquals(visaPan, pan.text.toString())
        assertEquals(19, pan.selectionEnd)
    }

    @Test
    fun `should switch from amex format to visa format when different pan is pasted over`() {
        shadowOf(getMainLooper()).waitForQueueUntilIdle(1)

        pan.setText(AMEX_PAN_FORMATTED)

        assertEquals(AMEX_PAN_FORMATTED, pan.text.toString())

        pan.setText("3528 0007 0000 0000 267")

        assertEquals("3528 0007 0000 0000 267", pan.text.toString())
        assertEquals(23, pan.selectionEnd)
    }

    @Test
    fun `should switch from visa format to amex format when different pan is pasted over`() {
        val visaPan = visaPan(formatted = true)

        pan.setText(visaPan)

        assertEquals(visaPan, pan.text.toString())

        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        pan.setText(AMEX_PAN_FORMATTED)

        assertEquals(AMEX_PAN_FORMATTED, pan.text.toString())
        assertEquals(AMEX_PAN_FORMATTED.length, pan.selectionEnd)
    }

    @Test
    fun `should be able to paste in an entire pan into a blank pan field and cursor goes to the end of the pan`() {
        shadowOf(getMainLooper()).waitForQueueUntilIdle(1)

        pan.setText(AMEX_PAN_FORMATTED)

        assertEquals(AMEX_PAN_FORMATTED, pan.text.toString())
        assertEquals(AMEX_PAN_FORMATTED.length, pan.selectionEnd)
    }

    @Test
    fun `should move the cursor to after space and digit if a digit has been entered in pan immediately before a space`() {
        pan.setText("1234 5678 90")
        pan.typeAtIndex(4, "1")

        assertEquals("1234 1567 890", pan.text.toString())
        assertEquals(6, pan.selectionEnd)
    }

    @Test
    fun `should move the cursor to after space if digit is entered in pan that forces a space after newly entered digit`() {
        pan.setText("1234 5678 90")
        pan.typeAtIndex(3, "1")

        assertEquals("1231 4567 890", pan.text.toString())
        assertEquals(5, pan.selectionEnd)
    }

    @Test
    fun `should not allow entering any more digits at the end of pan when limit is reached`() {
        pan.setText("1234 5678 9012 3456 789")

        pan.typeAtIndex(23, "1")

        assertEquals("1234 5678 9012 3456 789", pan.text.toString())
        assertEquals(23, pan.selectionEnd)
    }

    @Test
    fun `should move the cursor to the end of the pan when pan is pasted over and reduces the max length`() {
        val visaPan = visaPan(formatted = true)

        pan.setText(visaPan(formatted = false))

        assertEquals(visaPan, pan.text.toString())
        assertEquals(19, pan.selectionEnd)

        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        pan.setText(AMEX_PAN)

        assertEquals(AMEX_PAN_FORMATTED, pan.text.toString())
        assertEquals(AMEX_PAN_FORMATTED.length, pan.selectionEnd)
    }

    @Test
    fun `should do nothing when text is empty`() {
        pan.setText("")

        verify(cardValidationListener, never()).onBrandChange(any())
        verify(cardValidationListener, never()).onPanValidated(any())

        assertEquals("", pan.text.toString())
    }

    @Test
    fun `should shift digits to right by the number of pasted digits`() {
        pan.setText("4444 3333 2222 1111 000")
        assertEquals(23, pan.selectionEnd)

        pan.setSelection(8, 9)
        assertEquals(8, pan.selectionStart)
        assertEquals(9, pan.selectionEnd)

        pan.paste(pan.selectionStart, pan.selectionEnd, "8888")

        assertEquals("4444 3338 8882 2221 111", pan.text.toString())
        assertEquals(13, pan.selectionEnd)
    }

    @Test
    fun `should shift digits and trim if pasted characters change brand type`() {
        pan.setText("4444 3333 2222 1111 000")
        assertEquals(23, pan.selectionEnd)

        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        pan.paste(0, 14, "3434 3434 3434")

        assertEquals("3434 343434 34111", pan.text.toString())
        assertEquals(14, pan.selectionEnd)
    }

    @Test
    fun `should be able to add extra character at the end of valid pan`() {
        pan.setText("4444 3333 2222 1111")
        assertEquals(19, pan.selectionEnd)
        pan.typeAtIndex(19, "1")

        assertEquals("4444 3333 2222 1111 1", pan.text.toString())
        assertEquals(21, pan.selectionEnd)
    }

    @Test
    fun `should be able to add two characters at the end of valid pan`() {
        pan.setText("4444 3333 2222 1111")
        assertEquals(19, pan.selectionEnd)
        pan.typeAtIndex(19, "1")

        pan.typeAtIndex(21, "8")

        assertEquals("4444 3333 2222 1111 18", pan.text.toString())
        assertEquals(22, pan.selectionEnd)
    }

    @Test
    fun `should be able to add three characters at the end of valid pan`() {
        pan.setText("4444 3333 2222 1111")
        assertEquals(19, pan.selectionEnd)
        pan.typeAtIndex(19, "1")
        pan.typeAtIndex(21, "8")
        pan.typeAtIndex(22, "2")

        assertEquals("4444 3333 2222 1111 182", pan.text.toString())
        assertEquals(23, pan.selectionEnd)
    }

    @Test
    fun `should trim one character if we reach the max number of characters in pan`() {
        pan.setText("4444 3333 2222 1111")
        assertEquals(19, pan.selectionEnd)
        pan.typeAtIndex(19, "1")
        pan.typeAtIndex(21, "8")
        pan.typeAtIndex(22, "2")
        pan.typeAtIndex(23, "3")

        assertEquals("4444 3333 2222 1111 182", pan.text.toString())
        assertEquals(23, pan.selectionEnd)
    }

    @Test
    fun `should not be able to insert anything else than digits`() {
        pan.setText("4554 3333 2222 1111 000")
        assertEquals(23, pan.selectionEnd)
        pan.typeAtIndex(23, " ")

        assertEquals("4554 3333 2222 1111 000", pan.text.toString())
        assertEquals(23, pan.selectionEnd)
    }

    @Test
    fun `should not be able to insert non-digit`() {
        pan.setText("4554 3333 2222 1111 000")
        assertEquals(23, pan.selectionEnd)
        pan.typeAtIndex(4, "d")

        assertEquals("4554 3333 2222 1111 000", pan.text.toString())
        assertEquals(4, pan.selectionEnd)
    }

    @Test
    fun `should not be able to insert space`() {
        pan.setText("4554 3333 2222 1111 000")
        assertEquals(23, pan.selectionEnd)
        pan.typeAtIndex(3, " ")

        assertEquals("4554 3333 2222 1111 000", pan.text.toString())
        assertEquals(3, pan.selectionEnd)
    }

    @Test
    fun `should allow to insert the 8 and shifts the rest of the pan to the right by 1 digit`() {
        pan.setText("4444 3333 2222 1111 000")
        assertEquals(23, pan.selectionEnd)
        pan.typeAtIndex(5, "8")

        assertEquals("4444 8333 3222 2111 100", pan.text.toString())
        assertEquals(6, pan.selectionEnd)
    }

    // This test is to cover an issue where the caret position was incorrectly set
    // after the end of the text
    @Test
    fun `should move cursor at the end of the pan when appending pan which would exceed max length`() {
        pan.setText("4444")
        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        pan.append("3333222211110000")
        shadowOf(getMainLooper()).waitForQueueUntilIdle()

        assertEquals("4444 3333 2222 1111 000", pan.text.toString())
        assertEquals(23, pan.selectionStart)
    }
}
