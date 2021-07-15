package com.worldpay.access.checkout.client.validation.pan

import android.view.KeyEvent.KEYCODE_1
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.AMEX_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.toCardBrand
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN_FORMATTED
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN_FORMATTED
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PanFormattingIntegrationTest : AbstractValidationIntegrationTest() {

    @Before
    fun setup() {
        initialiseValidation(enablePanFormatting = true)
    }

    @Test
    fun `should be formatting pan with a space between every 4 digits - visa`() {
        pan.setText("4111111111111111")

        assertEquals("4111 1111 1111 1111", pan.text.toString())
        verify(cardValidationListener).onBrandChange(toCardBrand(VISA_BRAND))
        verify(cardValidationListener).onPanValidated(true)
    }

    @Test
    fun `should be formatting pan with a space between after 4, 6 and 5 digits`() {
        pan.setText("342793178931249")

        assertEquals("3427 931789 31249", pan.text.toString())
        verify(cardValidationListener).onBrandChange(toCardBrand(AMEX_BRAND))
        verify(cardValidationListener).onPanValidated(true)
    }

    @Test
    fun `should not be formatting pan when formatting is disabled`() {
        initialiseValidation(enablePanFormatting = false)

        pan.setText("4111111111111111")

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
    fun `should update the pan and set cursor position when formatted`() {
        pan.setText(VISA_PAN)

        assertEquals(VISA_PAN_FORMATTED, pan.text.toString())
        assertEquals(VISA_PAN_FORMATTED.length, pan.selectionEnd)
    }

    @Test
    fun `should move the cursor to after space and digit if a digit has been entered in pan immediately before a space`() {
        pan.setText("1234 5678 90")
        pan.typeAtIndex(4, KEYCODE_1)

        assertEquals("1234 1567 890", pan.text.toString())
        assertEquals(6, pan.selectionEnd)
    }

    @Test
    fun `should move the cursor to after space if digit is entered in pan that forces a space after newly entered digit`() {
        pan.setText("1234 5678 90")
        pan.typeAtIndex(3, KEYCODE_1)

        assertEquals("1231 4567 890", pan.text.toString())
        assertEquals(5, pan.selectionEnd)
    }

    @Test
    fun `should not allow entering any more digits at the end of pan when limit is reached`() {
        pan.setText("1234 5678 9012 3456 789")

        pan.typeAtIndex(23, KEYCODE_1)

        assertEquals("1234 5678 9012 3456 789", pan.text.toString())
        assertEquals(23, pan.selectionEnd)
    }

    @Test
    fun `should move the cursor to the end of the pan when pan is pasted over and reduces the max length`() {
        pan.setText(VISA_PAN)

        assertEquals(VISA_PAN_FORMATTED, pan.text.toString())
        assertEquals(VISA_PAN_FORMATTED.length, pan.selectionEnd)

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
    fun `should do nothing when text is blank`() {
        pan.setText(" ")

        verify(cardValidationListener, never()).onBrandChange(any())
        verify(cardValidationListener, never()).onPanValidated(any())

        assertEquals("", pan.text.toString())
    }
}
