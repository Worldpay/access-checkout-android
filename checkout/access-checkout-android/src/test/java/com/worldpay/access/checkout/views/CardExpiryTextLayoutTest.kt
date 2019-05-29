package com.worldpay.access.checkout.views

import android.text.InputFilter
import android.util.AttributeSet
import com.worldpay.access.checkout.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation

@RunWith(RobolectricTestRunner::class)
class CardExpiryTextLayoutTest {

    private lateinit var cardExpiryTextLayout: CardExpiryTextLayout
    private lateinit var cardViewListener: CardViewListener
    private val context = ShadowInstrumentation.getInstrumentation().context

    @Before
    fun setup() {
        cardExpiryTextLayout = CardExpiryTextLayout(context, mock(AttributeSet::class.java), 0)
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
    fun `should update card view listener on year loses focus`() {
        cardExpiryTextLayout.yearEditText.setText("29")

        cardExpiryTextLayout.yearEditTextOnFocusChange().onFocusChange(null, false)

        verify(cardViewListener).onEndUpdateDate(null, "29")
    }

    @Test
    fun `should update to success text color on valid month result`() {
        cardExpiryTextLayout.onMonthValidationResult(true)

        assertEquals(context.resources.getColor(R.color.SUCCESS, context.theme), cardExpiryTextLayout.monthEditText.currentTextColor)
    }

    @Test
    fun `should update to failure text color on invalid month result`() {
        cardExpiryTextLayout.onMonthValidationResult(false)

        assertEquals(context.resources.getColor(R.color.FAIL, context.theme), cardExpiryTextLayout.monthEditText.currentTextColor)
    }

    @Test
    fun `should update to success text color on valid year result`() {
        cardExpiryTextLayout.onYearValidationResult(true)

        assertEquals(context.resources.getColor(R.color.SUCCESS, context.theme), cardExpiryTextLayout.yearEditText.currentTextColor)
    }

    @Test
    fun `should update to failure text color on invalid year result`() {
        cardExpiryTextLayout.onYearValidationResult(false)

        assertEquals(context.resources.getColor(R.color.FAIL, context.theme), cardExpiryTextLayout.yearEditText.currentTextColor)
    }

    @Test
    fun `should update both month and year to success text color on valid full date validation result`() {
        cardExpiryTextLayout.onValidationResult(true)

        assertEquals(context.resources.getColor(R.color.SUCCESS, context.theme), cardExpiryTextLayout.monthEditText.currentTextColor)
        assertEquals(context.resources.getColor(R.color.SUCCESS, context.theme), cardExpiryTextLayout.yearEditText.currentTextColor)
    }

    @Test
    fun `should update both month and year to fail text color on invalid full date validation result`() {
        cardExpiryTextLayout.onValidationResult(false)

        assertEquals(context.resources.getColor(R.color.FAIL, context.theme), cardExpiryTextLayout.monthEditText.currentTextColor)
        assertEquals(context.resources.getColor(R.color.FAIL, context.theme), cardExpiryTextLayout.yearEditText.currentTextColor)
    }

    @Test
    fun `should set month length filters`() {
        val filter = InputFilter.LengthFilter(2)
        val filtersBefore: Array<InputFilter> = cardExpiryTextLayout.monthEditText.filters

        cardExpiryTextLayout.setMonthLengthFilter(filter)

        val filtersAfter: Array<InputFilter> = cardExpiryTextLayout.monthEditText.filters
        assertArrayEquals(filtersBefore.plus(filter), filtersAfter)
    }

    @Test
    fun `should set year length filters`() {
        val filter = InputFilter.LengthFilter(2)
        val filtersBefore: Array<InputFilter> = cardExpiryTextLayout.yearEditText.filters

        cardExpiryTextLayout.setYearLengthFilter(filter)

        val filtersAfter: Array<InputFilter> = cardExpiryTextLayout.yearEditText.filters
        assertArrayEquals(filtersBefore.plus(filter), filtersAfter)
    }

    @Test
    fun `get inserted text should return full date`() {
        cardExpiryTextLayout.monthEditText.setText("12")
        cardExpiryTextLayout.yearEditText.setText("20")

        assertEquals("12/20", cardExpiryTextLayout.getInsertedText())
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