package com.worldpay.access.checkout.views

import android.content.Context
import android.text.InputFilter
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.views.PANLayout.Companion.CARD_TAG
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import org.robolectric.Robolectric.buildAttributeSet as robolectricBuildAttributeSet

@RunWith(RobolectricTestRunner::class)
class PANLayoutTest {

    private lateinit var panLayout: PANLayoutTestInstance
    private lateinit var cardViewListener: CardViewListener
    private val context = ShadowInstrumentation.getInstrumentation().context

    @Before
    fun setup() {
        cardViewListener = mock(CardViewListener::class.java)
        panLayout = PANLayoutTestInstance(context)
        panLayout.cardViewListener = cardViewListener
    }

    @Test
    fun `should initialise using context`() {
        assertNotNull(panLayout)
    }

    @Test
    fun `should set unknown brand tag on image view after inflation`() {
        panLayout.finishInflate()

        assertEquals("card_unknown", panLayout.mImageView.getTag(CARD_TAG))
    }

    @Test
    fun `should update card view listener on text changed event`() {
        panLayout.mEditText.setText("123")

        verify(cardViewListener).onUpdatePAN("123")
    }

    @Test
    fun `should not update anything if not card view listener on text changed event`() {
        panLayout.cardViewListener = null

        panLayout.mEditText.setText("123")

        verifyZeroInteractions(cardViewListener)
    }

    @Test
    fun `should not update card view listener if on focus`() {
        panLayout.onFocusChangeListener().onFocusChange(null, true)

        verifyZeroInteractions(cardViewListener)
    }

    @Test
    fun `should not update anything if no card view listener set when loses focus`() {
        panLayout.cardViewListener = null
        panLayout.mEditText.setText("123")

        panLayout.onFocusChangeListener().onFocusChange(null, false)

        verifyZeroInteractions(cardViewListener)
    }

    @Test
    fun `should update card view listener on loses focus`() {
        panLayout.mEditText.setText("123")

        panLayout.onFocusChangeListener().onFocusChange(null, false)

        verify(cardViewListener).onEndUpdatePAN("123")
    }

    @Test
    fun `should update to success text color on valid result`() {
        panLayout.isValid(true)

        assertEquals(context.resources.getColor(R.color.SUCCESS, context.theme), panLayout.mEditText.currentTextColor)
    }

    @Test
    fun `should update to fail text color on invalid result`() {
        panLayout.isValid(false)

        assertEquals(context.resources.getColor(R.color.FAIL, context.theme), panLayout.mEditText.currentTextColor)
    }

    @Test
    fun `should update card tag`() {
        val logo = "card_mastercard"

        panLayout.applyCardLogo(logo)

        assertEquals(logo, panLayout.mImageView.getTag(CARD_TAG))
    }

    @Test
    fun `should set length filters`() {
        val filter = InputFilter.LengthFilter(3)
        val filtersBefore: Array<InputFilter> = panLayout.mEditText.filters

        panLayout.applyLengthFilter(filter)

        val filtersAfter: Array<InputFilter> = panLayout.mEditText.filters
        assertArrayEquals(filtersBefore.plus(filter), filtersAfter)
    }

}

class PANLayoutTestInstance(context: Context) : PANLayout(context, robolectricBuildAttributeSet().build(), 0) {

    fun finishInflate() {
        super.onFinishInflate()
    }
}