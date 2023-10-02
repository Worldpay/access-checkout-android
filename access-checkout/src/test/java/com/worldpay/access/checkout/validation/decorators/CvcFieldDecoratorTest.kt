package com.worldpay.access.checkout.validation.decorators

import android.text.InputFilter
import android.text.InputType
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.ui.AccessEditText
import com.worldpay.access.checkout.validation.filters.AccessCheckoutInputFilterFactory
import com.worldpay.access.checkout.validation.filters.CvcLengthFilter
import com.worldpay.access.checkout.validation.listeners.focus.CvcFocusChangeListener
import com.worldpay.access.checkout.validation.listeners.text.CvcTextWatcher
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CvcFieldDecoratorTest {

    private val cvcEditText = mock<AccessEditText>()
    private val panEditText = mock<AccessEditText>()

    private val cvcTextWatcher = mock<CvcTextWatcher>()
    private val cvcFocusChangeListener = mock<CvcFocusChangeListener>()
    private val accessCheckoutInputFilterFactory = AccessCheckoutInputFilterFactory()

    private lateinit var cvcFieldDecorator: CvcFieldDecorator

    @Before
    fun setup() {
        cvcFieldDecorator = createFieldDecorator()
    }

    // test name starts with 0 so that it is always run first
    @Test
    fun `0 - should add new text watchers when decorating cvc field each time`() {
        given(cvcEditText.filters).willReturn(emptyArray())

        var cvcFieldDecorator = createFieldDecorator()
        cvcFieldDecorator.decorate()

        verify(cvcEditText, never()).removeTextChangedListener(any())
        verify(cvcEditText).addTextChangedListener(cvcTextWatcher)

        reset(cvcEditText)

        given(cvcEditText.filters).willReturn(emptyArray())
        given(cvcEditText.text).willReturn("")

        cvcFieldDecorator = createFieldDecorator()
        cvcFieldDecorator.decorate()

        verify(cvcEditText).removeTextChangedListener(cvcTextWatcher)
        verify(cvcEditText).addTextChangedListener(cvcTextWatcher)
    }

    @Test
    fun `should not add hint to cvc field`() {
        given(cvcEditText.filters).willReturn(emptyArray())

        cvcFieldDecorator.decorate()

        verify(cvcEditText, never()).setHint(anyInt())
        verify(cvcEditText, never()).setHint(anyString())
    }

    @Test
    fun `should add filters when decorating cvc field`() {
        given(cvcEditText.filters).willReturn(emptyArray())

        val captor = argumentCaptor<Array<InputFilter>>()

        cvcFieldDecorator.decorate()

        verify(cvcEditText).filters = captor.capture()

        assertEquals(1, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is CvcLengthFilter)
    }

    @Test
    fun `should replace any length filters when decorating cvc field multiple times`() {
        given(cvcEditText.filters).willReturn(
            arrayOf(
                InputFilter.LengthFilter(1000),
                InputFilter.AllCaps(),
                CvcLengthFilter(panEditText)
            )
        )

        val captor = argumentCaptor<Array<InputFilter>>()

        cvcFieldDecorator.decorate()

        verify(cvcEditText).filters = captor.capture()

        assertEquals(2, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is InputFilter.AllCaps)
        assertTrue(captor.firstValue[1] is CvcLengthFilter)
    }

    @Test
    fun `should set expiry date field inputType to number when decorating`() {
        given(cvcEditText.filters).willReturn(emptyArray())
        cvcFieldDecorator.decorate()

        verify(cvcEditText, times(1)).inputType = InputType.TYPE_CLASS_NUMBER
    }

    @Test
    fun `should set text when the cvc field is in layout`() {
        given(cvcEditText.filters).willReturn(emptyArray())
        given(cvcEditText.isCursorVisible).willReturn(true)
        given(cvcEditText.text).willReturn("123")

        cvcFieldDecorator.decorate()

        verify(cvcEditText).isCursorVisible
        verify(cvcEditText).setText("123")
    }

    @Test
    fun `should not set text when the cvc field is not in layout`() {
        given(cvcEditText.filters).willReturn(emptyArray())
        given(cvcEditText.isCursorVisible).willReturn(false)

        cvcFieldDecorator.decorate()

        verify(cvcEditText).isCursorVisible
        verify(cvcEditText, never()).setText(any<String>())
    }

    @Test
    fun `should add focus change listener to cvc field`() {
        given(cvcEditText.filters).willReturn(emptyArray())
        given(cvcEditText.isCursorVisible).willReturn(false)

        cvcFieldDecorator.decorate()

        verify(cvcEditText).onFocusChangeListener = cvcFocusChangeListener
    }

    @Test
    fun `should call decorate when calling update function`() {
        given(cvcEditText.filters).willReturn(emptyArray())
        given(cvcEditText.isCursorVisible).willReturn(false)

        val fieldDecorator = spy(cvcFieldDecorator)

        fieldDecorator.update()

        verify(fieldDecorator).decorate()
    }

    private fun createFieldDecorator() = CvcFieldDecorator(
        cvcTextWatcher = cvcTextWatcher,
        cvcFocusChangeListener = cvcFocusChangeListener,
        cvcLengthFilter = accessCheckoutInputFilterFactory.getCvcLengthFilter(panEditText),
        cvcAccessEditText = cvcEditText
    )
}
