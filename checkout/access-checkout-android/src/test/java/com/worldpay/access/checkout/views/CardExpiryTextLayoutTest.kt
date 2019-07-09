package com.worldpay.access.checkout.views

import android.text.InputFilter
import android.view.View.FOCUSABLE
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import org.robolectric.Robolectric.buildAttributeSet as robolectricBuildAttributeSet

@RunWith(RobolectricTestRunner::class)
class CardExpiryTextLayoutTest {

    private lateinit var cardExpiryTextLayout: CardExpiryTextLayout
    private lateinit var cardViewListener: CardViewListener
    private val context = ShadowInstrumentation.getInstrumentation().context

    @Before
    fun setup() {
        cardExpiryTextLayout = CardExpiryTextLayout(context, robolectricBuildAttributeSet().build(), 0)
        cardViewListener = mock(CardViewListener::class.java)
        cardExpiryTextLayout.cardViewListener = cardViewListener
    }

    @Test
    fun `should initialise using context`() {
        assertNotNull(cardExpiryTextLayout)
    }

    @Test
    fun `should update card view listener on text changed event for month`() {
        cardExpiryTextLayout.monthEditText.setText("12")

        verify(cardViewListener).onUpdateDate("12", null)
    }

    @Test
    fun `should not update anything if not card view listener on text changed event for month`() {
        cardExpiryTextLayout.cardViewListener = null

        cardExpiryTextLayout.monthEditText.setText("12")

        verifyZeroInteractions(cardViewListener)
    }

    @Test
    fun `should update card view listener on text changed event for year`() {
        cardExpiryTextLayout.yearEditText.setText("29")

        verify(cardViewListener).onUpdateDate(null, "29")
    }

    @Test
    fun `should not update anything if not card view listener on text changed event for year`() {
        cardExpiryTextLayout.cardViewListener = null

        cardExpiryTextLayout.yearEditText.setText("29")

        verifyZeroInteractions(cardViewListener)
    }

    @Test
    fun `should not update card view listener if month on focus`() {
        cardExpiryTextLayout.monthEditTextOnFocusChange().onFocusChange(null, true)

        verifyZeroInteractions(cardViewListener)
    }

    @Test
    fun `should not update card view listener if year on focus`() {
        cardExpiryTextLayout.yearEditTextOnFocusChange().onFocusChange(null, true)

        verifyZeroInteractions(cardViewListener)
    }

    @Test
    fun `should not update anything if no card view listener set when month loses focus`() {
        cardExpiryTextLayout.cardViewListener = null

        cardExpiryTextLayout.monthEditTextOnFocusChange().onFocusChange(null, false)

        verifyZeroInteractions(cardViewListener)
    }

    @Test
    fun `should not update anything if no card view listener set when year loses focus`() {
        cardExpiryTextLayout.cardViewListener = null

        cardExpiryTextLayout.yearEditTextOnFocusChange().onFocusChange(null, false)

        verifyZeroInteractions(cardViewListener)
    }

    @Test
    fun `should update card view listener on month loses focus`() {
        cardExpiryTextLayout.monthEditText.setText("12")

        cardExpiryTextLayout.monthEditTextOnFocusChange().onFocusChange(null, false)

        verify(cardViewListener).onEndUpdateDate("12", null)
    }

    @Test
    fun `should not update card view listener if month loses focus and moves to year field`() {
        cardExpiryTextLayout.monthEditText.setText("12")
        cardExpiryTextLayout.yearEditText.focusable = FOCUSABLE
        cardExpiryTextLayout.yearEditText.requestFocus()

        cardExpiryTextLayout.monthEditTextOnFocusChange().onFocusChange(null, false)

        verify(cardViewListener, times(0)).onEndUpdateDate(any(), any())
    }

    @Test
    fun `should update card view listener on year loses focus`() {
        cardExpiryTextLayout.yearEditText.setText("29")

        cardExpiryTextLayout.yearEditTextOnFocusChange().onFocusChange(null, false)

        verify(cardViewListener).onEndUpdateDate(null, "29")
    }

    @Test
    fun `should not update card view listener if year loses focus and moves to month field`() {
        cardExpiryTextLayout.yearEditText.setText("29")
        cardExpiryTextLayout.monthEditText.focusable = FOCUSABLE
        cardExpiryTextLayout.monthEditText.requestFocus()

        cardExpiryTextLayout.yearEditTextOnFocusChange().onFocusChange(null, false)

        verify(cardViewListener, times(0)).onEndUpdateDate(any(), any())
    }

    @Test
    fun `should update to success text color on valid result`() {
        cardExpiryTextLayout.isValid(true)

        assertEquals(context.resources.getColor(R.color.SUCCESS, context.theme), cardExpiryTextLayout.monthEditText.currentTextColor)
        assertEquals(context.resources.getColor(R.color.SUCCESS, context.theme), cardExpiryTextLayout.yearEditText.currentTextColor)
    }

    @Test
    fun `should update to failure text color on invalid result`() {
        cardExpiryTextLayout.isValid(false)

        assertEquals(context.resources.getColor(R.color.FAIL, context.theme), cardExpiryTextLayout.monthEditText.currentTextColor)
        assertEquals(context.resources.getColor(R.color.FAIL, context.theme), cardExpiryTextLayout.yearEditText.currentTextColor)
    }

    @Test
    fun `should set length filters`() {
        val filter = InputFilter.LengthFilter(2)
        val monthFiltersBefore: Array<InputFilter> = cardExpiryTextLayout.monthEditText.filters
        val yearFiltersBefore: Array<InputFilter> = cardExpiryTextLayout.yearEditText.filters

        cardExpiryTextLayout.applyLengthFilter(filter)

        val monthFiltersAfter: Array<InputFilter> = cardExpiryTextLayout.monthEditText.filters
        val yearFiltersAfter: Array<InputFilter> = cardExpiryTextLayout.yearEditText.filters
        assertArrayEquals(monthFiltersBefore.plus(filter), monthFiltersAfter)
        assertArrayEquals(yearFiltersBefore.plus(filter), yearFiltersAfter)
    }

    @Test
    fun `should be able to parse a month field`() {
        cardExpiryTextLayout.monthEditText.setText("12")

        assertEquals(12, cardExpiryTextLayout.getMonth())
    }

    @Test
    fun `should be able to parse a year field`() {
        cardExpiryTextLayout.yearEditText.setText("29")

        assertEquals(2029, cardExpiryTextLayout.getYear())
    }

}