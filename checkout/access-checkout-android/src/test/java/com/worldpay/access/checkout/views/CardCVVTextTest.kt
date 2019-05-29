package com.worldpay.access.checkout.views

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import com.worldpay.access.checkout.R
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class CardCVVTextTest {

    private lateinit var cardCVVText: CardCVVText
    private val context = ShadowInstrumentation.getInstrumentation().context

    @Before
    fun setup() {
        cardCVVText = CardCVVText(context)
    }

    @Test
    fun `should initialise using context`() {
        assertNotNull(cardCVVText)
    }

    @Test
    fun `should initialise using context and attribute set`() {
        val cardCVVText = CardCVVText(context, mock(AttributeSet::class.java))

        assertNotNull(cardCVVText)
    }

    @Test
    fun `should initialise using context and attribute set and style set`() {
        val cardCVVText = CardCVVText(context, mock(AttributeSet::class.java), 0)

        assertNotNull(cardCVVText)
    }

    @Test
    fun `should update card view listener on text changed event`() {
        val cardViewListener = mock(CardViewListener::class.java)
        cardCVVText.cardViewListener = cardViewListener

        cardCVVText.setText("123")

        verify(cardViewListener).onUpdateCVV("123")
    }

    @Test
    fun `should not update anything if not card view listener on text changed event`() {
        cardCVVText.setText("123") // no assertion as no listener defined (test used for jacoco coverage)
    }

    @Test
    fun `should not update card view listener if on focus`() {
        val cardViewListener = mock(CardViewListener::class.java)
        cardCVVText.cardViewListener = cardViewListener

        cardCVVText.onFocusChangeListener().onFocusChange(null, true)

        verifyZeroInteractions(cardViewListener)
    }

    @Test
    fun `should not update anything if no card view listener set when loses focus`() {
        cardCVVText.setText("123")

        cardCVVText.onFocusChangeListener().onFocusChange(null, false)// no assertion as no listener defined (test used for jacoco coverage)
    }

    @Test
    fun `should update card view listener on loses focus`() {
        val cardViewListener = mock(CardViewListener::class.java)
        cardCVVText.setText("123")
        cardCVVText.cardViewListener = cardViewListener

        cardCVVText.onFocusChangeListener().onFocusChange(null, false)

        verify(cardViewListener).onEndUpdateCVV("123")
    }

    @Test
    fun `should update to success text color on valid result`() {
        cardCVVText.onValidationResult(true)

        assertEquals(context.resources.getColor(R.color.SUCCESS, context.theme), cardCVVText.currentTextColor)
    }

    @Test
    fun `should update to fail text color on invalid result`() {
        cardCVVText.onValidationResult(false)

        assertEquals(context.resources.getColor(R.color.FAIL, context.theme), cardCVVText.currentTextColor)
    }

    @Test
    fun `should set length filters`() {
        val filter = InputFilter.LengthFilter(3)
        val filtersBefore: Array<InputFilter> = cardCVVText.filters

        cardCVVText.setLengthFilter(filter)

        val filtersAfter: Array<InputFilter> = cardCVVText.filters
        assertArrayEquals(filtersBefore.plus(filter), filtersAfter)
    }
}
