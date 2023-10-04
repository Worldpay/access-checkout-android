package com.worldpay.access.checkout.validation.decorators

import android.text.InputFilter
import android.text.InputType
import com.worldpay.access.checkout.ui.AccessEditText
import com.worldpay.access.checkout.validation.filters.AccessCheckoutInputFilterFactory
import com.worldpay.access.checkout.validation.filters.ExpiryDateLengthFilter
import com.worldpay.access.checkout.validation.listeners.focus.ExpiryDateFocusChangeListener
import com.worldpay.access.checkout.validation.listeners.text.ExpiryDateTextWatcher
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
class ExpiryDateFieldDecoratorTest {

    private val expiryDateEditText = mock<AccessEditText>()

    private val expiryDateTextWatcher = mock<ExpiryDateTextWatcher>()
    private val expiryDateFocusChangeListener = mock<ExpiryDateFocusChangeListener>()
    private val accessCheckoutInputFilterFactory = AccessCheckoutInputFilterFactory()

    private lateinit var expiryDateFieldDecorator: ExpiryDateFieldDecorator

    @Before
    fun setup() {
        expiryDateFieldDecorator = createFieldDecorator()
    }

    // test name starts with 0 so that it is always run first
    @Test
    fun `0 - should add new text watchers when decorating expiry date field each time`() {
        given(expiryDateEditText.filters).willReturn(emptyArray())

        var expiryDateFieldDecorator = createFieldDecorator()
        expiryDateFieldDecorator.decorate()

        verify(expiryDateEditText, never()).removeTextChangedListener(any())
        verify(expiryDateEditText).addTextChangedListener(expiryDateTextWatcher)

        reset(expiryDateEditText)

        given(expiryDateEditText.filters).willReturn(emptyArray())
        given(expiryDateEditText.text).willReturn("")

        expiryDateFieldDecorator = createFieldDecorator()
        expiryDateFieldDecorator.decorate()

        verify(expiryDateEditText).removeTextChangedListener(expiryDateTextWatcher)
        verify(expiryDateEditText).addTextChangedListener(expiryDateTextWatcher)
    }

    @Test
    fun `should not add hint to expiry date field`() {
        given(expiryDateEditText.filters).willReturn(emptyArray())

        expiryDateFieldDecorator.decorate()

        verify(expiryDateEditText, never()).setHint(anyInt())
        verify(expiryDateEditText, never()).setHint(anyString())
    }

    @Test
    fun `should add filters when decorating expiry date field`() {
        given(expiryDateEditText.filters).willReturn(emptyArray())

        val captor = argumentCaptor<Array<InputFilter>>()

        expiryDateFieldDecorator.decorate()

        verify(expiryDateEditText).filters = captor.capture()

        assertEquals(1, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is ExpiryDateLengthFilter)
    }

    @Test
    fun `should set expiry date field inputType to number when decorating`() {
        given(expiryDateEditText.filters).willReturn(emptyArray())
        expiryDateFieldDecorator.decorate()

        verify(expiryDateEditText, times(1)).inputType = InputType.TYPE_CLASS_NUMBER
    }

    @Test
    fun `should replace any length filters when decorating expiry date field multiple times`() {
        given(expiryDateEditText.filters).willReturn(
            arrayOf(
                InputFilter.LengthFilter(1000),
                InputFilter.AllCaps(),
                ExpiryDateLengthFilter()
            )
        )

        val captor = argumentCaptor<Array<InputFilter>>()

        expiryDateFieldDecorator.decorate()

        verify(expiryDateEditText).filters = captor.capture()

        assertEquals(2, captor.firstValue.size)
        assertTrue(captor.firstValue[0] is InputFilter.AllCaps)
        assertTrue(captor.firstValue[1] is ExpiryDateLengthFilter)
    }

    @Test
    fun `should set text when the expiry date field is in layout`() {
        given(expiryDateEditText.filters).willReturn(emptyArray())
        given(expiryDateEditText.isCursorVisible).willReturn(true)
        given(expiryDateEditText.text).willReturn("12/21")

        expiryDateFieldDecorator.decorate()

        verify(expiryDateEditText).isCursorVisible
        verify(expiryDateEditText).setText("12/21")
    }

    @Test
    fun `should not set text when the expiry date field is not in layout`() {
        given(expiryDateEditText.filters).willReturn(emptyArray())
        given(expiryDateEditText.isCursorVisible).willReturn(false)

        expiryDateFieldDecorator.decorate()

        verify(expiryDateEditText).isCursorVisible
        verify(expiryDateEditText, never()).setText(any<String>())
    }

    @Test
    fun `should add focus change listener to expiry date field`() {
        given(expiryDateEditText.filters).willReturn(emptyArray())
        given(expiryDateEditText.isCursorVisible).willReturn(false)

        expiryDateFieldDecorator.decorate()

        verify(expiryDateEditText).onFocusChangeListener = expiryDateFocusChangeListener
    }

    @Test
    fun `should call decorate when calling update function`() {
        given(expiryDateEditText.filters).willReturn(emptyArray())
        given(expiryDateEditText.isCursorVisible).willReturn(false)

        val fieldDecorator = spy(expiryDateFieldDecorator)

        fieldDecorator.update()

        verify(fieldDecorator).decorate()
    }

    private fun createFieldDecorator() = ExpiryDateFieldDecorator(
        expiryDateTextWatcher = expiryDateTextWatcher,
        expiryDateFocusChangeListener = expiryDateFocusChangeListener,
        expiryDateLengthFilter = accessCheckoutInputFilterFactory.getExpiryDateLengthFilter(),
        expiryDateAccessEditText = expiryDateEditText
    )
}
