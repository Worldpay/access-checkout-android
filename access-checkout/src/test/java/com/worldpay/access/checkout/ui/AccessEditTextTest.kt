package com.worldpay.access.checkout.ui

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.method.KeyListener
import android.view.View
import android.widget.EditText
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AccessEditTextTest {
    private lateinit var accessEditText: AccessEditText
    private var contextMock: Context = mock()
    private var editTextMock: EditText = mock()
    private var attributeValuesMock: AttributeValues = mock()

    @Before
    fun setUp() {
        accessEditText = AccessEditText(contextMock, null, 0, editTextMock, attributeValuesMock)
    }

    /**
     * Constructors tests
     */
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

    /**
     * Properties tests
     */
    @Test
    fun `text should return EditText text`() {
        val editable = mock<Editable>()
        given(editable.toString()).willReturn("some text")
        given(editTextMock.text).willReturn(editable)

        assertEquals("some text", accessEditText.text)
    }

    @Test
    fun `setText() should call EditText setText()`() {
        accessEditText.setText("some text")

        verify(editTextMock).setText("some text")
    }

    @Test
    fun `selectionStart should return EditText selectionStart`() {
        given(editTextMock.selectionStart).willReturn(12)

        assertEquals(12, accessEditText.selectionStart)
    }

    @Test
    fun `selectionEnd should return EditText selectionEnd`() {
        given(editTextMock.selectionEnd).willReturn(12)

        assertEquals(12, accessEditText.selectionEnd)
    }

    @Test
    fun `setSelection() should call EditText setSelection()`() {
        accessEditText.setSelection(4, 7)

        verify(editTextMock).setSelection(4, 7)
    }

    @Test
    fun `isCursorVisible should return EditText isCursorVisible`() {
        given(editTextMock.isCursorVisible).willReturn(true)

        assertTrue(accessEditText.isCursorVisible)
    }

    @Test
    fun `currentTextColor should return EditText currentTextColor`() {
        given(editTextMock.currentTextColor).willReturn(Color.BLACK)

        assertEquals(Color.BLACK, accessEditText.currentTextColor)
    }

    @Test
    fun `setTextColor() should set EditText color`() {
        accessEditText.setTextColor(Color.BLACK)

        verify(editTextMock).setTextColor(Color.BLACK)
    }

    @Test
    fun `filters getter should return EditText filters`() {
        val filters = arrayOf(mock<InputFilter>())
        given(editTextMock.filters).willReturn(filters)

        assertEquals(filters, accessEditText.filters)
    }

    @Test
    fun `filters setter should set EditText filters`() {
        val filters = arrayOf(mock<InputFilter>())
        accessEditText.filters = filters

        verify(editTextMock).filters = filters
    }

    @Test
    fun `inputType getter should return EditText inputType`() {
        given(editTextMock.inputType).willReturn(InputType.TYPE_CLASS_NUMBER)

        assertEquals(InputType.TYPE_CLASS_NUMBER, accessEditText.inputType)
    }

    @Test
    fun `inputType setter should set EditText inputType`() {
        accessEditText.inputType = InputType.TYPE_CLASS_NUMBER

        verify(editTextMock).inputType = InputType.TYPE_CLASS_NUMBER
    }

    @Test
    fun `keyListener getter should return EditText keyListener`() {
        val expectedListener = mock<KeyListener>()
        given(editTextMock.keyListener).willReturn(expectedListener)

        assertEquals(expectedListener, accessEditText.keyListener)
    }

    @Test
    fun `keyListener setter should set EditText keyListener`() {
        val expectedListener = mock<KeyListener>()
        accessEditText.keyListener = expectedListener

        verify(editTextMock).keyListener = expectedListener
    }

    @Test
    fun `hint getter should return EditText hint`() {
        val hint = "card-number"
        given(editTextMock.hint).willReturn(hint)

        assertEquals(hint, accessEditText.getHint())
    }

    @Test
    fun `hint setter should set EditText hint using a string`() {
        val hint = "card-number"
        accessEditText.setHint(hint)

        verify(editTextMock).hint = hint
    }

    @Test
    fun `hint setter should set EditText hint using a resourceId`() {
        val resourceId = 1234
        accessEditText.setHint(resourceId)

        verify(editTextMock).setHint(resourceId)
    }

    /**
    Methods tests
     */
    @Test
    fun `length() should return EditText length`() {
        given(editTextMock.length()).willReturn(123)

        assertEquals(123, accessEditText.length())
    }

    @Test
    fun `clear() should clear EditText text`() {
        val editableMock = mock<Editable>()
        given(editTextMock.text).willReturn(editableMock)

        accessEditText.clear()
        verify(editableMock).clear()
    }

    @Test
    fun `getOnFocusChangeListener() should call EditText getOnFocusChangeListener()`() {
        given(editTextMock.onFocusChangeListener).willReturn(mock())
        accessEditText.onFocusChangeListener

        verify(editTextMock).onFocusChangeListener
    }

    @Test
    fun `dispatchKeyEvent() should call EditText dispatchKeyEvent()`() {
        val listener = mock<View.OnFocusChangeListener>()
        accessEditText.onFocusChangeListener = listener

        verify(editTextMock).onFocusChangeListener = listener
    }
}
