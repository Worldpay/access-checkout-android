package com.worldpay.access.checkout.ui

import android.graphics.Color
import android.text.InputType
import android.text.method.DigitsKeyListener
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation

@RunWith(RobolectricTestRunner::class)
class AccessEditTextTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private lateinit var accessEditText: AccessEditText

    @Before
    fun setUp() {
        accessEditText = AccessEditText(context)
    }

    @Test
    fun `should return current text colour when called`() {
        accessEditText.setTextColor(Color.BLACK)

        assertEquals(Color.BLACK, accessEditText.currentTextColor)
    }

    @Test
    fun `should return text of AccessEditText when called`() {
        accessEditText.inputType = InputType.TYPE_CLASS_NUMBER

        assertEquals(InputType.TYPE_CLASS_NUMBER, accessEditText.inputType)
    }

    @Test
    fun `should clear the text of AccessEditText when called`() {
        accessEditText.setText(visaPan())
        assertEquals(visaPan(), accessEditText.text)

        accessEditText.clear()
        assertEquals("", accessEditText.text)
    }

    @Test
    fun `should set the hint of AccessEditText with a string`() {
        accessEditText.setHint("Card Number")
        assertEquals("Card Number", accessEditText.getHint())
    }

    @Test
    fun `should set the hint text of AccessEditText using a resourceId`() {
        val expectedHint = "some-hint"

        accessEditText.setHint(R.string.some_hint)

        assertEquals(expectedHint, accessEditText.getHint())
    }

    @Test
    fun `should get the key listener of AccessEditText`() {
        val expectedListener = DigitsKeyListener.getInstance("0123456789")
        accessEditText.keyListener = expectedListener

        assertEquals(expectedListener, accessEditText.keyListener)
    }
}
