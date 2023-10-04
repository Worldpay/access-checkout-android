package com.worldpay.access.checkout.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.text.Editable
import android.text.Editable.Factory
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.core.content.withStyledAttributes
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AccessEditTextTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private lateinit var accessEditText: AccessEditText
    private var contextMock: Context = mock<Context>()
    private var editText: EditText = EditText(contextMock)
    private var editTextSpy: EditText = spy(editText)
//    private var contextSpy: Context = spy(contextMock)


    @Before
    fun setUp() {
        accessEditText = AccessEditText(context)
    }

    @Test
    fun `should set the color of EditText when called`() {
        accessEditText.setTextColor(Color.BLACK)
        verify(editTextSpy).setTextColor(Color.BLACK)
    }

    @Test
    fun `should get the color from EditText when called`() {
        whenever(editTextSpy.currentTextColor).thenReturn(Color.BLACK)

        assertEquals(Color.BLACK, accessEditText.currentTextColor)
        verify(editTextSpy).currentTextColor
    }

    @Test
    fun `should set the input type of EditText when called`() {
        accessEditText.inputType = InputType.TYPE_CLASS_NUMBER
        verify(editTextSpy, times(1)).inputType = InputType.TYPE_CLASS_NUMBER

        assertEquals(InputType.TYPE_CLASS_NUMBER, accessEditText.inputType)
    }

    @Test
    fun `should get the input type of EditText when called`() {
        whenever(editTextSpy.inputType).thenReturn(InputType.TYPE_CLASS_NUMBER)

        assertEquals(InputType.TYPE_CLASS_NUMBER, accessEditText.inputType)
        verify(editTextSpy, times(1)).inputType
    }

    @Test
    fun `should clear the text of EditText when called`() {
//        val editableTextMock = mock<Editable>()
//        val editableText = Factory().newEditable(visaPan())
//        whenever(editTextSpy.getText()).thenReturn(editableText)
        editTextSpy.setText("4444")

//        val remove = accessEditText.text

        accessEditText.clearText()
        verify(editTextSpy, times(1)).text
        assertEquals("", accessEditText.text)
    }

    @Test
    fun `should get the hint of EditText when called`() {
        val hint = "card-number"
        whenever(editTextSpy.hint).thenReturn(hint)

        assertEquals(hint, accessEditText.getHint())
        verify(editTextSpy).hint
    }

    @Test
    fun `should set the hint of EditText using a string`() {
        val hint = "card-number"
        accessEditText.setHint(hint)

        verify(editTextSpy).setHint(hint)
    }

//    @Test
//    fun `should set the hint text of EditText using a resourceId`() {
//        @StringRes val  resourceId = 1234
//        val hint: CharSequence = "11"
////
////        val resources: Resources = mock<Resources>()
////
////        whenever(resources.getText(resourceId)).thenReturn(hint)
////        whenever(contextSpy.resources).thenReturn(resources)
////        whenever(contextSpy.resources.getText(resourceId)).thenReturn(hint)
////
////        given(contextSpy.resources.getText(anyInt())).willReturn("custom-hint")
////        doReturn("custom-hint").whenever(contextSpy).resources.getText(anyInt())
//        whenever(contextMock.resources.getText(resourceId)).thenReturn("custom-hint")
//
//        accessEditText.setHint(resourceId)
//
//        verify(editTextSpy).setHint(resourceId)
//    }

    @Test
    fun `should get the key listener of AccessEditText`() {
        val expectedListener = DigitsKeyListener.getInstance("0123456789")
        accessEditText.keyListener = expectedListener

        assertEquals(expectedListener, accessEditText.keyListener)
    }
}
