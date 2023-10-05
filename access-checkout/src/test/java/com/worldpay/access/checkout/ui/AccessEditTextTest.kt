package com.worldpay.access.checkout.ui

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.method.KeyListener
import android.widget.EditText
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AccessEditTextTest {
    private lateinit var accessEditText: AccessEditText
    private var contextMock: Context = mock<Context>()
    private var editTextMock: EditText = mock<EditText>()

    @Before
    fun setUp() {
        accessEditText = AccessEditText(contextMock, editTextMock)
//        whenever(contextMock.withStyledAttributes(any(),any(),any())).then()
    }

    @Test
    fun `should set the color of EditText when called`() {
        accessEditText.setTextColor(Color.BLACK)

        verify(editTextMock).setTextColor(Color.BLACK)
    }

    @Test
    fun `should get the color from EditText when called`() {
        whenever(editTextMock.currentTextColor).thenReturn(Color.BLACK)

        assertEquals(Color.BLACK, accessEditText.currentTextColor)
    }

    @Test
    fun `should set the input type of EditText when called`() {
        accessEditText.inputType = InputType.TYPE_CLASS_NUMBER

        verify(editTextMock).inputType = InputType.TYPE_CLASS_NUMBER
    }

    @Test
    fun `should get the input type of EditText when called`() {
        whenever(editTextMock.inputType).thenReturn(InputType.TYPE_CLASS_NUMBER)

        assertEquals(InputType.TYPE_CLASS_NUMBER, accessEditText.inputType)
    }

    @Test
    fun `should clear the text of EditText when called`() {
        val editableMock = mock<Editable>()
        whenever(editTextMock.text).thenReturn(editableMock)

        accessEditText.clearText()
        verify(editableMock).clear()
    }

    @Test
    fun `should get the hint of EditText when called`() {
        val hint = "card-number"
        whenever(editTextMock.hint).thenReturn(hint)

        assertEquals(hint, accessEditText.getHint())
    }

    @Test
    fun `should set the hint of EditText using a string`() {
        val hint = "card-number"
        accessEditText.setHint(hint)

        verify(editTextMock).hint = hint
    }

    @Test
    fun `should set the hint text of EditText using a resourceId`() {
        val resourceId = 1234
        accessEditText.setHint(resourceId)

        verify(editTextMock).setHint(resourceId)
    }

    @Test
    fun `should get the key listener of AccessEditText`() {
        val expectedListener = mock<KeyListener>()
        whenever(editTextMock.keyListener).thenReturn(expectedListener)

        assertEquals(expectedListener, accessEditText.keyListener)
    }

    @Test
    fun `should set the keyListener of EditText`() {
        val expectedListener = mock<KeyListener>()
        accessEditText.keyListener = expectedListener

        verify(editTextMock).keyListener = expectedListener
    }
}
