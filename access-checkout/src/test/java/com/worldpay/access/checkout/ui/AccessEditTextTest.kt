package com.worldpay.access.checkout.ui

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.method.KeyListener
import android.widget.EditText
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AccessEditTextTest {
    private lateinit var accessEditText: AccessEditText
    private var contextMock: Context = mock()
    private var editTextMock: EditText = mock()
    private var attributeValuesMock: AttributeValues = mock()

    @Before
    fun setUp() {
        accessEditText = AccessEditText(contextMock, null, 0, editTextMock, attributeValuesMock)
    }

    @Test
    fun `should set the color of EditText when called`() {
        accessEditText.setTextColor(Color.BLACK)

        verify(editTextMock).setTextColor(Color.BLACK)
    }

    @Test
    fun `should get the color from EditText when called`() {
        given(editTextMock.currentTextColor).willReturn(Color.BLACK)

        assertEquals(Color.BLACK, accessEditText.currentTextColor)
    }

    @Test
    fun `should set the input type of EditText when called`() {
        accessEditText.inputType = InputType.TYPE_CLASS_NUMBER

        verify(editTextMock).inputType = InputType.TYPE_CLASS_NUMBER
    }

    @Test
    fun `should get the input type of EditText when called`() {
        given(editTextMock.inputType).willReturn(InputType.TYPE_CLASS_NUMBER)

        assertEquals(InputType.TYPE_CLASS_NUMBER, accessEditText.inputType)
    }

    @Test
    fun `should clear the text of EditText when called`() {
        val editableMock = mock<Editable>()
        given(editTextMock.text).willReturn(editableMock)

        accessEditText.clearText()
        verify(editableMock).clear()
    }

    @Test
    fun `should get the hint of EditText when called`() {
        val hint = "card-number"
        given(editTextMock.hint).willReturn(hint)

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
        given(editTextMock.keyListener).willReturn(expectedListener)

        assertEquals(expectedListener, accessEditText.keyListener)
    }

    @Test
    fun `should set the keyListener of EditText`() {
        val expectedListener = mock<KeyListener>()
        accessEditText.keyListener = expectedListener

        verify(editTextMock).keyListener = expectedListener
    }

    @Test
    fun `should set properties with attribute values in constructor`() {
        val contextMock: Context = mock()
        val attributeValuesMock: AttributeValues = mock()
        val editTextMock: EditText = mock()

        given(attributeValuesMock.stringOf("hint")).willReturn("some hint value")

        AccessEditText(contextMock, null, 0, editTextMock, attributeValuesMock)

        verify(editTextMock).hint = "some hint value"
    }

    @Test
    fun `should obtain instance by passing two values to the constructor`() {
        assertNotNull(AccessEditText(contextMock, null))
    }
}
