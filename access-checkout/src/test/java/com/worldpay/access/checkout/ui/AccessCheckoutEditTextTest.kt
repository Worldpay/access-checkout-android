package com.worldpay.access.checkout.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.method.KeyListener
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import com.worldpay.access.checkout.R
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AccessCheckoutEditTextTest {
    private lateinit var accessCheckoutEditText: AccessCheckoutEditText
    private var contextMock: Context = mock()
    private var editTextMock: EditText = mock()
    private var attributeSetMock: AttributeSet = mock()
    private var typedArrayMock: TypedArray = mock()

    @Before
    fun setUp() {
        given(
            contextMock.obtainStyledAttributes(
                attributeSetMock,
                R.styleable.AccessCheckoutEditText,
                0,
                0
            )
        ).willReturn(typedArrayMock)

        accessCheckoutEditText =
            AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)
    }

    /**
     * Constructors tests
     */
    @Test
    fun `should construct an instance by passing Context, AttributeSet`() {
        assertNotNull(AccessCheckoutEditText(contextMock, null))
    }

    @Test
    fun `should construct an instance by passing Context`() {
        assertNotNull(AccessCheckoutEditText(contextMock))
    }

    @Test
    fun `constructor should set editText instance`() {
        assertEquals(editTextMock, accessCheckoutEditText.editText)
    }

    /**
     * Attribute setting tests
     */
    @Test
    fun `should set text color from attribute set`() {
        given(
            typedArrayMock.getColor(
                eq(R.styleable.AccessCheckoutEditText_android_textColor),
                eq(0)
            )
        ).willReturn(Color.RED)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).setTextColor(Color.RED)
    }

    @Test
    fun `should set hint from attribute set`() {
        given(typedArrayMock.getString(eq(R.styleable.AccessCheckoutEditText_android_hint))).willReturn("some-hint")

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).setHint("some-hint")
    }

    @Test
    fun `should set ems from attribute set`() {
        given(typedArrayMock.getInt(eq(R.styleable.AccessCheckoutEditText_android_ems), eq(0))).willReturn(123)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).setEms(123)
    }

    @Test
    fun `should set hintTextColor from attribute set`() {
        given(typedArrayMock.getColor(eq(R.styleable.AccessCheckoutEditText_android_textColorHint), eq(0))).willReturn(
            Color.GREEN
        )

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).setHintTextColor(Color.GREEN)
    }

    @Test
    fun `should set imeOptions from attribute set`() {
        given(typedArrayMock.getInt(eq(R.styleable.AccessCheckoutEditText_android_imeOptions), eq(0))).willReturn(123)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).imeOptions = 123
    }

    @Test
    fun `should set cursorVisible from attribute set`() {
        given(
            typedArrayMock.getBoolean(
                eq(R.styleable.AccessCheckoutEditText_android_cursorVisible),
                eq(true)
            )
        ).willReturn(true)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).isCursorVisible = true
    }

    @Test
    fun `should set textScale from attribute set`() {
        given(typedArrayMock.getFloat(eq(R.styleable.AccessCheckoutEditText_android_textScaleX), eq(0F))).willReturn(1F)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).textScaleX = 1F
    }

    @Test
    fun `should set textSize from attribute set`() {
        given(typedArrayMock.getDimension(eq(R.styleable.AccessCheckoutEditText_android_textSize), eq(0F))).willReturn(1F)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).textSize = 1F
    }

    @Test
    fun `should set padding from attribute set`() {
        given(typedArrayMock.getDimension(eq(R.styleable.AccessCheckoutEditText_android_padding), eq(0.0F))).willReturn(1F)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).setPadding(1, 1, 1, 1)
    }

    @Test
    fun `should set padding left, top, right, bottom from attribute set`() {
        given(typedArrayMock.getDimension(eq(R.styleable.AccessCheckoutEditText_android_paddingLeft), eq(0.0F))).willReturn(1F)
        given(typedArrayMock.getDimension(eq(R.styleable.AccessCheckoutEditText_android_paddingTop), eq(0.0F))).willReturn(2F)
        given(typedArrayMock.getDimension(eq(R.styleable.AccessCheckoutEditText_android_paddingRight), eq(0.0F))).willReturn(3F)
        given(typedArrayMock.getDimension(eq(R.styleable.AccessCheckoutEditText_android_paddingBottom), eq(0.0F))).willReturn(4F)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).setPadding(1, 2, 3, 4)
    }

    /**
     * Properties tests
     */
    @Test
    fun `companion object has an editTextPartialId field`() {
        assertNotNull(AccessCheckoutEditText.editTextPartialId)
    }

    @Test
    fun `text should return EditText text`() {
        val editable = mock<Editable>()
        given(editable.toString()).willReturn("some text")
        given(editTextMock.text).willReturn(editable)

        assertEquals("some text", accessCheckoutEditText.text)
    }

    @Test
    fun `setText() should call EditText setText()`() {
        accessCheckoutEditText.setText("some text")

        verify(editTextMock).setText("some text")
    }

    @Test
    fun `selectionStart should return EditText selectionStart`() {
        given(editTextMock.selectionStart).willReturn(12)

        assertEquals(12, accessCheckoutEditText.selectionStart)
    }

    @Test
    fun `selectionEnd should return EditText selectionEnd`() {
        given(editTextMock.selectionEnd).willReturn(12)

        assertEquals(12, accessCheckoutEditText.selectionEnd)
    }

    @Test
    fun `setSelection() should call EditText setSelection()`() {
        accessCheckoutEditText.setSelection(4, 7)

        verify(editTextMock).setSelection(4, 7)
    }

    @Test
    fun `isCursorVisible should return EditText isCursorVisible`() {
        given(editTextMock.isCursorVisible).willReturn(true)

        assertTrue(accessCheckoutEditText.isCursorVisible)
    }

    @Test
    fun `currentTextColor should return EditText currentTextColor`() {
        given(editTextMock.currentTextColor).willReturn(Color.BLACK)

        assertEquals(Color.BLACK, accessCheckoutEditText.currentTextColor)
    }

    @Test
    fun `setTextColor() should set EditText color`() {
        accessCheckoutEditText.setTextColor(Color.BLACK)

        verify(editTextMock).setTextColor(Color.BLACK)
    }

    @Test
    fun `filters getter should return EditText filters`() {
        val filters = arrayOf(mock<InputFilter>())
        given(editTextMock.filters).willReturn(filters)

        assertEquals(filters, accessCheckoutEditText.filters)
    }

    @Test
    fun `filters setter should set EditText filters`() {
        val filters = arrayOf(mock<InputFilter>())
        accessCheckoutEditText.filters = filters

        verify(editTextMock).filters = filters
    }

    @Test
    fun `inputType getter should return EditText inputType`() {
        given(editTextMock.inputType).willReturn(InputType.TYPE_CLASS_NUMBER)

        assertEquals(InputType.TYPE_CLASS_NUMBER, accessCheckoutEditText.inputType)
    }

    @Test
    fun `inputType setter should set EditText inputType`() {
        accessCheckoutEditText.inputType = InputType.TYPE_CLASS_NUMBER

        verify(editTextMock).inputType = InputType.TYPE_CLASS_NUMBER
    }

    @Test
    fun `keyListener getter should return EditText keyListener`() {
        val expectedListener = mock<KeyListener>()
        given(editTextMock.keyListener).willReturn(expectedListener)

        assertEquals(expectedListener, accessCheckoutEditText.keyListener)
    }

    @Test
    fun `keyListener setter should set EditText keyListener`() {
        val expectedListener = mock<KeyListener>()
        accessCheckoutEditText.keyListener = expectedListener

        verify(editTextMock).keyListener = expectedListener
    }

    @Test
    fun `hint getter should return EditText hint`() {
        val hint = "card-number"
        given(editTextMock.hint).willReturn(hint)

        assertEquals(hint, accessCheckoutEditText.getHint())
    }

    @Test
    fun `hint setter should set EditText hint using a string`() {
        val hint = "card-number"
        accessCheckoutEditText.setHint(hint)

        verify(editTextMock).hint = hint
    }

    @Test
    fun `hint setter should set EditText hint using a resourceId`() {
        val resourceId = 1234
        accessCheckoutEditText.setHint(resourceId)

        verify(editTextMock).setHint(resourceId)
    }

    @Test
    fun `imeOptions getter should return EditText imeOptions`() {
        given(editTextMock.imeOptions).willReturn(123)

        assertEquals(123, accessCheckoutEditText.imeOptions)
    }

    @Test
    fun `imeOptions setter should set EditText imeOptions`() {
        accessCheckoutEditText.imeOptions = 123

        verify(editTextMock).imeOptions = 123
    }

    @Test
    fun `textLocale getter should return EditText textLocale`() {
        given(editTextMock.textLocale).willReturn(Locale.ENGLISH)

        assertEquals(Locale.ENGLISH, accessCheckoutEditText.textLocale)
    }

    @Test
    fun `textLocale setter should set EditText textLocale`() {
        accessCheckoutEditText.textLocale = Locale.ENGLISH

        verify(editTextMock).textLocale = Locale.ENGLISH
    }

    @Test
    fun `textScaleX getter should return EditText textScaleX`() {
        given(editTextMock.textScaleX).willReturn(1F)

        assertEquals(1F, accessCheckoutEditText.textScaleX)
    }

    @Test
    fun `textScaleX setter should set EditText textScaleX`() {
        accessCheckoutEditText.textScaleX = 1.1F

        verify(editTextMock).textScaleX = 1.1F
    }

    @Test
    fun `textSize getter should return EditText textSize`() {
        given(editTextMock.textSize).willReturn(1F)

        assertEquals(1F, accessCheckoutEditText.textSize)
    }

    @Test
    fun `textSize setter should set EditText textSize`() {
        accessCheckoutEditText.textSize = 1.1F

        verify(editTextMock).textSize = 1.1F
    }

    @Test
    fun `autoSizeTextType getter should return EditText autoSizeTextType`() {
        given(editTextMock.autoSizeTextType).willReturn(123)

        assertEquals(123, accessCheckoutEditText.autoSizeTextType)
    }

    @Test
    fun `currentHintTextColor getter should return EditText currentHintTextColor`() {
        given(editTextMock.currentHintTextColor).willReturn(123)

        assertEquals(123, accessCheckoutEditText.currentHintTextColor)
    }

    @Test
    fun `typeface getter should return EditText typeface`() {
        val typefaceMock = mock<Typeface>()
        given(editTextMock.typeface).willReturn(typefaceMock)

        assertEquals(typefaceMock, accessCheckoutEditText.typeface)
    }

    @Test
    fun `typeface setter should set EditText typeface`() {
        val typefaceMock = mock<Typeface>()
        accessCheckoutEditText.typeface = typefaceMock

        verify(editTextMock).typeface = typefaceMock
    }

    @Test
    fun `isCursorVisible getter should return EditText isCursorVisible`() {
        given(editTextMock.isCursorVisible).willReturn(true)

        assertEquals(true, accessCheckoutEditText.isCursorVisible)
    }

    @Test
    fun `isCursorVisible setter should set EditText isCursorVisible`() {
        accessCheckoutEditText.isCursorVisible = true

        verify(editTextMock).isCursorVisible = true
    }

    /**
     Methods tests
     */
    @Test
    fun `length() should return EditText length`() {
        given(editTextMock.length()).willReturn(123)

        assertEquals(123, accessCheckoutEditText.length())
    }

    @Test
    fun `clear() should clear EditText text`() {
        val editableMock = mock<Editable>()
        given(editTextMock.text).willReturn(editableMock)

        accessCheckoutEditText.clear()
        verify(editableMock).clear()
    }

    @Test
    fun `getOnFocusChangeListener() should call EditText getOnFocusChangeListener()`() {
        given(editTextMock.onFocusChangeListener).willReturn(mock())
        accessCheckoutEditText.onFocusChangeListener

        verify(editTextMock).onFocusChangeListener
    }

    @Test
    fun `setOnFocusChangeListener() should call EditText setOnFocusChangeListener()`() {
        val mockListener = mock<View.OnFocusChangeListener>()
        accessCheckoutEditText.onFocusChangeListener = mockListener

        verify(editTextMock).onFocusChangeListener = mockListener
    }

    @Test
    fun `dispatchKeyEvent() should call EditText dispatchKeyEvent()`() {
        val keyEvent = mock<KeyEvent>()
        accessCheckoutEditText.dispatchKeyEvent(keyEvent)

        verify(editTextMock).dispatchKeyEvent(keyEvent)
    }

    @Test
    fun `onSaveInstanceState() should call EditText onSaveInstanceState()`() {
        accessCheckoutEditText.onSaveInstanceState()

        verify(editTextMock).onSaveInstanceState()
    }

    @Test
    fun `onRestoreInstanceState() should call EditText onRestoreInstanceState()`() {
        val bundledState = mock<Bundle>()
        val editTextState = mock<Bundle>()
        given(bundledState.getBundle("editTextState")).willReturn(editTextState)

        accessCheckoutEditText.onRestoreInstanceState(bundledState)

        verify(editTextMock).onRestoreInstanceState(editTextState)
    }

    @Test
    fun `setEms should call EditText setEms()`() {
        accessCheckoutEditText.setEms(4)

        verify(editTextMock).setEms(4)
    }

    @Test
    fun `setHintTextColor should call EditText setHintTextColor()`() {
        accessCheckoutEditText.setHintTextColor(Color.BLACK)

        verify(editTextMock).setHintTextColor(Color.BLACK)
    }

    @Test
    fun `setAutoSizeTextTypeWithDefaults should call EditText setAutoSizeTextTypeWithDefaults()`() {
        accessCheckoutEditText.setAutoSizeTextTypeWithDefaults(123)

        verify(editTextMock).setAutoSizeTextTypeWithDefaults(123)
    }

    @Test
    fun `setTextAppearance should call EditText setTextAppearance()`() {
        accessCheckoutEditText.setTextAppearance(123)

        verify(editTextMock).setTextAppearance(123)
    }
}
