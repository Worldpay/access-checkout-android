package com.worldpay.access.checkout.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Color.GREEN
import android.graphics.Color.RED
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.method.KeyListener
import android.util.AttributeSet
import android.view.AbsSavedState
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.util.BuildVersionProvider
import com.worldpay.access.checkout.util.BuildVersionProviderHolder
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.never
import org.mockito.kotlin.*
import org.mockito.kotlin.eq
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AccessCheckoutEditTextTest {
    private lateinit var accessCheckoutEditText: AccessCheckoutEditText
    private var contextMock: Context = mock()
    private var editTextMock: EditText = mock()
    private var attributeSetMock: AttributeSet = mock()
    private var typedArrayMock: TypedArray = mock()
    private var buildProviderMock: BuildVersionProvider = mock()

    @Before
    fun setUp() {
        BuildVersionProviderHolder.instance = buildProviderMock

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
        mockColorAttributeValue(R.styleable.AccessCheckoutEditText_android_textColor, RED)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).setTextColor(RED)
    }

    @Test
    fun `should set hint from attribute set`() {
        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_hint, "some-hint")

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).setHint("some-hint")
    }

    @Test
    fun `should set autofill hint from attribute set when sdk version is 26 or higher`() {
        whenever(buildProviderMock.isAtLeastO()).thenReturn(true)

        mockAttributeValue(
            R.styleable.AccessCheckoutEditText_android_autofillHints,
            "some-credit-card"
        )

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).setAutofillHints("some-credit-card")
    }

    @Test
    fun `should not set autofill hint from attribute set when sdk version is lower than 26`() {
        whenever(buildProviderMock.isAtLeastO()).thenReturn(false)

        mockAttributeValue(
            R.styleable.AccessCheckoutEditText_android_autofillHints,
            "some-credit-card"
        )

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock, never()).setAutofillHints("some-credit-card")
    }

    @Test
    fun `should set autofill hint from hint constants set when sdk version is 26 or higher`() {
        whenever(buildProviderMock.isAtLeastO()).thenReturn(true)

        val field =
            AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)
        field.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_NUMBER)

        verify(editTextMock).setAutofillHints("creditCardNumber")
    }

    @Test
    fun `should not set autofill hint from hint constants set when sdk version is lower than 26`() {
        whenever(buildProviderMock.isAtLeastO()).thenReturn(false)

        val field =
            AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)
        field.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_NUMBER)

        verify(editTextMock, never()).setAutofillHints("creditCardNumber")
    }

    @Test
    fun `setAutofillHints() should call EditText setAutofillHints() when sdk version is 26 or higher`() {
        whenever(buildProviderMock.isAtLeastO()).thenReturn(true)

        accessCheckoutEditText.setAutofillHints("some-credit-card")

        verify(editTextMock).setAutofillHints("some-credit-card")
    }

    @Test
    fun `setAutofillHints() should call EditText setAutofillHints() when sdk version is lower than 26`() {
        whenever(buildProviderMock.isAtLeastO()).thenReturn(false)

        accessCheckoutEditText.setAutofillHints("some-credit-card")

        verify(editTextMock, never()).setAutofillHints("some-credit-card")
    }

    @Test
    fun `getAutofillHints() should call EditText getAutofillHints() when sdk version is 26 or higher`() {
        whenever(buildProviderMock.isAtLeastO()).thenReturn(true)

        val autofillHints = arrayOf("creditCardNumber", "creditCardExpirationDate")
        whenever(editTextMock.autofillHints).thenReturn(autofillHints)
        accessCheckoutEditText.setAutofillHints("creditCardNumber", "creditCardExpirationDate")

        val result = accessCheckoutEditText.autofillHints

        assertArrayEquals(autofillHints, result)
        verify(editTextMock).autofillHints
    }

    @Test
    fun `getAutofillHints() not should call EditText getAutofillHints() when sdk version is lower than 26`() {
        whenever(buildProviderMock.isAtLeastO()).thenReturn(false)

        val result = accessCheckoutEditText.autofillHints

        verify(editTextMock, never()).autofillHints
        assertNull(result)
    }

    @Test
    fun `getAutofillHints() should not call EditText getAutofillHints()`() {
        val contextMock = mock<Context>()
        val attributeSetMock = mock<AttributeSet>()

        val accessCheckoutEditTextOptional =
            AccessCheckoutEditText(contextMock, attributeSetMock, 0, null)

        val result = accessCheckoutEditTextOptional.autofillHints

        assertNull(result)
    }

    @Test
    fun `setAutofillHints() should not call EditText setAutofillHints()`() {
        whenever(buildProviderMock.isAtLeastO()).thenReturn(true)

        val contextMock = mock<Context>()
        val attributeSetMock = mock<AttributeSet>()

        val accessCheckoutEditTextOptional =
            AccessCheckoutEditText(contextMock, attributeSetMock, 0, null)

        accessCheckoutEditTextOptional.setAutofillHints("patata")

        val result = accessCheckoutEditTextOptional.autofillHints

        assertNull(result)
    }

    @Test
    fun `should set hintTextColor from attribute set`() {
        mockColorAttributeValue(R.styleable.AccessCheckoutEditText_android_textColorHint, GREEN)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).setHintTextColor(GREEN)
    }

    @Test
    fun `should set imeOptions from attribute set`() {
        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_imeOptions, 123)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).imeOptions = 123
    }

    @Test
    fun `should set cursorVisible from attribute set`() {
        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_cursorVisible, true)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).isCursorVisible = true
    }

    @Test
    fun `should set textSize from attribute set`() {
        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_textSize, 1F)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).textSize = 1F
    }

    @Test
    fun `should set padding from attribute set`() {
        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_padding, 1F)

        val accessCheckoutEditText =
            AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        assertParentPaddingValue(accessCheckoutEditText)

        verify(editTextMock).setPadding(1, 1, 1, 1)
    }

    @Test
    fun `should set padding left, top, right, bottom from attributes set`() {
        // We need to mock other padding attributes that would interfere as not set
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_padding)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingStart)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingEnd)

        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_paddingLeft, 1F)
        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_paddingTop, 2F)
        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_paddingRight, 3F)
        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_paddingBottom, 4F)

        val accessCheckoutEditText =
            AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        assertParentPaddingValue(accessCheckoutEditText)

        verify(editTextMock).setPadding(1, 2, 3, 4)
    }

    @Test
    fun `should set padding right using setPadding from attribute set`() {
        // We need to mock other padding attributes that would interfere as not set
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_padding)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingStart)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingEnd)

        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_paddingRight, 3F)

        val accessCheckoutEditText =
            AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        assertParentPaddingValue(accessCheckoutEditText)

        verify(editTextMock).setPadding(0, 0, 3, 0)
    }

    @Test
    fun `should set paddingRelative when paddingStart and paddingEnd are supplied from attribute set`() {
        // We need to mock other padding attributes that would interfere as not set
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_padding)

        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_paddingStart, 1F)
        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_paddingEnd, 3F)

        val accessCheckoutEditText =
            AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        assertParentPaddingRelativeValue(accessCheckoutEditText)

        verify(editTextMock).setPaddingRelative(1, 0, 3, 0)
    }

    @Test
    fun `should set paddingRelative when only paddingStart is supplied from attribute set`() {
        // We need to mock other padding attributes that would interfere as not set
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_padding)

        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_paddingStart, 1F)

        val accessCheckoutEditText =
            AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        assertParentPaddingRelativeValue(accessCheckoutEditText)

        verify(editTextMock).setPaddingRelative(1, 0, 0, 0)
    }

    @Test
    fun `should set paddingRelative when only paddingEnd is supplied from attribute set`() {
        // We need to mock other padding attributes that would interfere as not set
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_padding)

        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_paddingEnd, 1F)

        val accessCheckoutEditText =
            AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        assertParentPaddingRelativeValue(accessCheckoutEditText)

        verify(editTextMock).setPaddingRelative(0, 0, 1, 0)
    }

    @Test
    fun `should use editText padding values as defaults when only paddingStart defined`() {
        // We need to mock other padding attributes that would interfere as not set
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_padding)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingEnd)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingLeft)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingTop)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingRight)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingBottom)

        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_paddingStart, 1F)
        given(editTextMock.paddingTop).willReturn(2)
        given(editTextMock.paddingEnd).willReturn(3)
        given(editTextMock.paddingBottom).willReturn(4)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).setPaddingRelative(1, 2, 3, 4)
    }

    @Test
    fun `should use editText padding values as defaults when only paddingEnd defined`() {
        // We need to mock other padding attributes that would interfere as not set
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_padding)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingStart)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingLeft)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingTop)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingRight)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingBottom)

        given(editTextMock.paddingStart).willReturn(1)
        given(editTextMock.paddingTop).willReturn(2)
        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_paddingEnd, 3F)
        given(editTextMock.paddingBottom).willReturn(4)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).setPaddingRelative(1, 2, 3, 4)
    }

    @Test
    fun `should use editText padding values as defaults when only paddingLeft defined`() {
        // We need to mock other padding attributes that would interfere as not set
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_padding)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingStart)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingEnd)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingTop)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingRight)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingBottom)

        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_paddingLeft, 1F)
        given(editTextMock.paddingTop).willReturn(2)
        given(editTextMock.paddingRight).willReturn(3)
        given(editTextMock.paddingBottom).willReturn(4)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).setPadding(1, 2, 3, 4)
    }

    @Test
    fun `should use editText padding values as defaults when only paddingRight defined`() {
        // We need to mock other padding attributes that would interfere as not set
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_padding)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingStart)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingEnd)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingLeft)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingTop)
        mockDimensionAttributeNotSet(R.styleable.AccessCheckoutEditText_android_paddingBottom)

        given(editTextMock.paddingLeft).willReturn(1)
        given(editTextMock.paddingTop).willReturn(2)
        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_paddingRight, 3F)
        given(editTextMock.paddingBottom).willReturn(4)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).setPadding(1, 2, 3, 4)
    }

    @Test
    fun `should set font from attribute set when sdk version is 26 or higher`() {
        whenever(buildProviderMock.isAtLeastO()).thenReturn(true)

        val typefaceMock: Typeface = mock()
        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_font, typefaceMock)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).typeface = typefaceMock
    }

    @Test
    fun `should not set font from attribute set when sdk version is lower than 26`() {
        whenever(buildProviderMock.isAtLeastO()).thenReturn(false)

        val typefaceMock: Typeface = mock()
        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_font, typefaceMock)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock, never()).typeface = typefaceMock
    }

    @Test
    fun `should set background from attribute set`() {
        val drawable: Drawable = mock()
        mockAttributeValue(R.styleable.AccessCheckoutEditText_android_background, drawable)

        AccessCheckoutEditText(contextMock, attributeSetMock, 0, editTextMock)

        verify(editTextMock).background = drawable
    }

    /**
     * Properties tests
     */
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

    @Test
    fun `background getter should return EditText background`() {
        val backgroundMock = mock<Drawable>()
        given(editTextMock.background).willReturn(backgroundMock)

        assertEquals(backgroundMock, accessCheckoutEditText.background)
    }

    @Test
    fun `background setter should set EditText background`() {
        val backgroundMock = mock<Drawable>()
        accessCheckoutEditText.background = backgroundMock

        verify(editTextMock).background = backgroundMock
    }

    @Test
    fun `background getter should return null when EditText is null`() {
        val nullEditText = null
        val accessCheckoutEditText = AccessCheckoutEditText(mock(), mock(), 0, nullEditText)

        assertNull(accessCheckoutEditText.background)
    }

    @Test
    fun `background setter should do nothing when EditText is null`() {
        val nullEditText = null
        val accessCheckoutEditText = AccessCheckoutEditText(mock(), mock(), 0, nullEditText)

        accessCheckoutEditText.background = mock()
    }

    @Test
    fun `isEnabled getter should return EditText isEnabled`() {
        given(editTextMock.isEnabled).willReturn(true)

        assertEquals(true, accessCheckoutEditText.isEnabled)
    }

    @Test
    fun `isEnabled setter should set EditText isEnabled`() {
        accessCheckoutEditText.isEnabled = true

        verify(editTextMock).isEnabled = true
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
        val argumentCaptor = ArgumentCaptor.forClass(SpannableStringBuilder::class.java)

        accessCheckoutEditText.clear()

        verify(accessCheckoutEditText.editText)!!.text = argumentCaptor.capture()
        assertEquals(0, argumentCaptor.value.length)
    }

    @Test
    fun `getOnFocusChangeListener() should call not call inner EditText getOnFocusChangeListener()`() {
        val mockListener = mock<View.OnFocusChangeListener>()

        given(editTextMock.onFocusChangeListener).willReturn(mock())
        accessCheckoutEditText.onFocusChangeListener = mockListener

        //Trigger getter
        accessCheckoutEditText.onFocusChangeListener

        verifyNoInteractions(editTextMock.onFocusChangeListener)
    }

    @Test
    fun `setOnFocusChangeListener() should not call inner EditText setOnFocusChangeListener()`() {
        val mockListener = mock<View.OnFocusChangeListener>()
        accessCheckoutEditText.onFocusChangeListener = mockListener

        assertEquals(null, editTextMock.onFocusChangeListener)
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
    fun `onRestoreInstanceState() should call EditText onRestoreInstanceState() using key used to save EditText state`() {
        val editTextState = mock<AbsSavedState>()

        val bundledState = mock<Bundle>()
        given(bundledState.getParcelable<Parcelable>("editTextState")).willReturn(editTextState)

        accessCheckoutEditText.onRestoreInstanceState(bundledState)

        verify(editTextMock).onRestoreInstanceState(editTextState)
    }

    @Test
    fun `setHintTextColor should call EditText setHintTextColor()`() {
        accessCheckoutEditText.setHintTextColor(Color.BLACK)

        verify(editTextMock).setHintTextColor(Color.BLACK)
    }

    @Test
    fun `setAutoSizeTextTypeWithDefaults should call EditText setAutoSizeTextTypeWithDefaults() when sdk version is 26 or higher`() {
        whenever(buildProviderMock.isAtLeastO()).thenReturn(true)

        accessCheckoutEditText.setAutoSizeTextTypeWithDefaults(123)

        verify(editTextMock).setAutoSizeTextTypeWithDefaults(123)
    }

    @Test
    fun `setAutoSizeTextTypeWithDefaults should not call EditText setAutoSizeTextTypeWithDefaults() when sdk versions is lower than 26`() {
        whenever(buildProviderMock.isAtLeastO()).thenReturn(false)

        BuildVersionProviderHolder.instance = buildProviderMock

        accessCheckoutEditText.setAutoSizeTextTypeWithDefaults(123)

        verify(editTextMock, never()).setAutoSizeTextTypeWithDefaults(123)
    }

    @Test
    fun `setTextAppearance should call EditText setTextAppearance() when sdk version is 23 or higher`() {
        whenever(buildProviderMock.isAtLeastM()).thenReturn(true)

        accessCheckoutEditText.setTextAppearance(123)

        verify(editTextMock).setTextAppearance(123)
    }

    @Test
    fun `setTextAppearance should not call EditText setTextAppearance() when sdk version is lower than 23`() {
        whenever(buildProviderMock.isAtLeastM()).thenReturn(false)

        accessCheckoutEditText.setTextAppearance(123)

        verify(editTextMock, never()).setTextAppearance(123)
    }

    @Test
    fun `setPadding should call EditText setPadding()`() {
        accessCheckoutEditText.setPadding(1, 2, 3, 4)

        assertParentPaddingValue(accessCheckoutEditText)

        verify(editTextMock).setPadding(1, 2, 3, 4)
    }

    @Test
    fun `setPaddingRelative should call EditText setPaddingRelative()`() {
        accessCheckoutEditText.setPaddingRelative(1, 2, 3, 4)

        assertParentPaddingRelativeValue(accessCheckoutEditText)

        verify(editTextMock).setPaddingRelative(1, 2, 3, 4)
    }

    @Test
    fun `setBackgroundColor should call EditText setBackgroundColor()`() {
        accessCheckoutEditText.setBackgroundColor(123)

        verify(editTextMock).setBackgroundColor(123)
    }

    @Test
    fun `setBackgroundResource should call EditText setBackgroundResource()`() {
        accessCheckoutEditText.setBackgroundResource(123)

        verify(editTextMock).setBackgroundResource(123)
    }

    private fun assertParentPaddingValue(accessCheckoutEditText: AccessCheckoutEditText) {
        assertEquals(accessCheckoutEditText.paddingLeft, 0)
        assertEquals(accessCheckoutEditText.paddingTop, 0)
        assertEquals(accessCheckoutEditText.paddingRight, 0)
        assertEquals(accessCheckoutEditText.paddingBottom, 0)
    }

    private fun assertParentPaddingRelativeValue(accessCheckoutEditText: AccessCheckoutEditText) {
        assertEquals(accessCheckoutEditText.paddingStart, 0)
        assertEquals(accessCheckoutEditText.paddingTop, 0)
        assertEquals(accessCheckoutEditText.paddingEnd, 0)
        assertEquals(accessCheckoutEditText.paddingBottom, 0)
    }

    private fun mockAttributeValue(attributeId: Int, value: String) {
        given(typedArrayMock.getString(eq(attributeId))).willReturn(value)
    }

    private fun mockColorAttributeValue(attributeId: Int, value: Int) {
        given(typedArrayMock.getColor(eq(attributeId), anyInt())).willReturn(value)
    }

    private fun mockAttributeValue(attributeId: Int, value: Int) {
        given(typedArrayMock.getInt(eq(attributeId), anyInt())).willReturn(value)
    }

    private fun mockAttributeValue(attributeId: Int, value: Boolean) {
        given(typedArrayMock.getBoolean(eq(attributeId), anyBoolean())).willReturn(value)
    }

    private fun mockAttributeValue(attributeId: Int, value: Float) {
        given(typedArrayMock.getDimension(eq(attributeId), anyFloat())).willReturn(value)
    }

    private fun mockAttributeValue(attributeId: Int, typefaceMock: Typeface) {
        given(typedArrayMock.getFont(attributeId)).willReturn(typefaceMock)
    }

    private fun mockAttributeValue(attributeId: Int, drawable: Drawable) {
        given(typedArrayMock.getDrawable(attributeId)).willReturn(drawable)
    }

    private fun mockDimensionAttributeNotSet(attributeId: Int) {
        // We first need to make sure that the padding attribute has not been set
        // Value -1F, as per production code, is a default value to indicate attribute is not set
        mockAttributeValue(attributeId, -1F)
    }
}
