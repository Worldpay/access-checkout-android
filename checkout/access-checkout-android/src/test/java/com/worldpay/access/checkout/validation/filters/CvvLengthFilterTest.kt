package com.worldpay.access.checkout.validation.filters

import android.widget.EditText
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class CvvLengthFilterTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val cvv = EditText(context)
    private val pan = EditText(context)

    @Before
    fun setup() {
        cvv.filters = arrayOf(CvvLengthFilter(pan, CARD_CONFIG_BASIC))
    }

    @Test
    fun `should limit to max length`() {
        cvv.setText("123456")
        assertEquals("1234", cvv.text.toString())
    }

    @Test
    fun `should allow text within limit`() {
        cvv.setText("1")
        assertEquals("1", cvv.text.toString())

        cvv.setText("12")
        assertEquals("12", cvv.text.toString())

        cvv.setText("123")
        assertEquals("123", cvv.text.toString())

        cvv.setText("1234")
        assertEquals("1234", cvv.text.toString())
    }

    @Test
    fun `should change the max length depending on the pan detected`() {
        pan.setText("")

        cvv.setText("123456")
        assertEquals("1234", cvv.text.toString())

        pan.setText(VISA_PAN)

        cvv.setText("123456")
        assertEquals("123", cvv.text.toString())
    }

    @Test
    fun `should ignore pan if no pan edit text is passed to filter`() {
        cvv.filters = arrayOf(CvvLengthFilter(null, CARD_CONFIG_BASIC))

        pan.setText("")

        cvv.setText("123456")
        assertEquals("1234", cvv.text.toString())

        pan.setText(VISA_PAN)

        cvv.setText("123456")
        assertEquals("1234", cvv.text.toString())
    }

}
