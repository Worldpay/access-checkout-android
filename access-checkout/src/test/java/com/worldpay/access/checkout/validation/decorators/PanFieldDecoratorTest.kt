package com.worldpay.access.checkout.validation.decorators

import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.widget.EditText
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import com.worldpay.access.checkout.validation.filters.AccessCheckoutInputFilterFactory
import com.worldpay.access.checkout.validation.filters.PanNumericFilter
import com.worldpay.access.checkout.validation.listeners.focus.PanFocusChangeListener
import com.worldpay.access.checkout.validation.listeners.text.PanTextWatcher
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.*

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PanFieldDecoratorTest {

    private val panEditText = mock<EditText>()

    private val panTextWatcher = mock<PanTextWatcher>()
    private val panFocusChangeListener = mock<PanFocusChangeListener>()
    private val accessCheckoutInputFilterFactory = AccessCheckoutInputFilterFactory()

    private lateinit var panFieldDecorator: PanFieldDecorator

    @Before
    fun setup() {
        panFieldDecorator = createFieldDecorator()
    }

    // test name starts with 0 so that it is always run first
    @Test
    fun `0 - should add new text watchers when decorating pan field each time`() {
        given(panEditText.filters).willReturn(emptyArray())

        createFieldDecorator().decorate()

        verify(panEditText, never()).removeTextChangedListener(any())
        verify(panEditText).addTextChangedListener(panTextWatcher)

        reset(panEditText)

        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.text).willReturn(mock())
        given(mock<Editable>().toString()).willReturn("")

        createFieldDecorator().decorate()

        verify(panEditText).removeTextChangedListener(panTextWatcher)
        verify(panEditText).addTextChangedListener(panTextWatcher)
    }

    @Test
    fun `should not add hint to pan field`() {
        given(panEditText.filters).willReturn(emptyArray())

        panFieldDecorator.decorate()

        verify(panEditText, never()).setHint(anyInt())
        verify(panEditText, never()).hint = anyString()
    }

    @Test
    fun `should add filters when decorating pan field`() {
        given(panEditText.filters).willReturn(emptyArray())

        val captor = argumentCaptor<Array<InputFilter>>()

        panFieldDecorator.decorate()

        verify(panEditText).filters = captor.capture()

        assertEquals(1, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is PanNumericFilter)
    }

    @Test
    fun `should replace any length filters when decorating pan field multiple times`() {
        given(panEditText.filters).willReturn(
            arrayOf(
                InputFilter.LengthFilter(1000),
                InputFilter.AllCaps(),
                PanNumericFilter()
            )
        )

        val captor = argumentCaptor<Array<InputFilter>>()

        panFieldDecorator.decorate()

        verify(panEditText).filters = captor.capture()

        assertEquals(2, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is InputFilter.AllCaps)
        assertTrue(captor.firstValue[1] is PanNumericFilter)
    }

    @Test
    fun `should set pan field inputType to number when decorating`() {
        panFieldDecorator = createFieldDecorator(panFormattingEnabled = false)
        given(panEditText.filters).willReturn(emptyArray())

        panFieldDecorator.decorate()

        verify(panEditText, times(1)).inputType = InputType.TYPE_CLASS_NUMBER
    }

    @Test
    fun `should set pan field inputType to datetime when decorating if pan formatting is enabled`() {
        panFieldDecorator = createFieldDecorator(panFormattingEnabled = true)
        given(panEditText.filters).willReturn(emptyArray())

        panFieldDecorator.decorate()

        verify(panEditText, times(1)).inputType = InputType.TYPE_CLASS_DATETIME
    }

    @Test
    fun `should set text when the pan field is in layout`() {
        val panEditable = mock<Editable>()
        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.isCursorVisible).willReturn(true)
        given(panEditText.text).willReturn(panEditable)
        given(panEditable.toString()).willReturn(visaPan())

        panFieldDecorator.decorate()

        verify(panEditText).isCursorVisible
        verify(panEditText).setText(visaPan())
    }

    @Test
    fun `should not set text when the pan field is not in layout`() {
        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.isCursorVisible).willReturn(false)

        panFieldDecorator.decorate()

        verify(panEditText).isCursorVisible
        verify(panEditText, never()).setText(any<String>())
    }

    @Test
    fun `should add focus change listener to pan field`() {
        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.isCursorVisible).willReturn(false)

        panFieldDecorator.decorate()

        verify(panEditText).onFocusChangeListener = panFocusChangeListener
    }

    @Test
    fun `should call decorate when calling update function`() {
        given(panEditText.filters).willReturn(emptyArray())
        given(panEditText.isCursorVisible).willReturn(false)

        val fieldDecorator = spy(panFieldDecorator)

        fieldDecorator.update()

        verify(fieldDecorator).decorate()
    }

    private fun createFieldDecorator(panFormattingEnabled: Boolean = false) = PanFieldDecorator(
        panTextWatcher = panTextWatcher,
        panFocusChangeListener = panFocusChangeListener,
        panNumericFilter = accessCheckoutInputFilterFactory.getPanNumericFilter(),
        panEditText = panEditText,
        panFormattingEnabled
    )
}
