package com.worldpay.access.checkout.validation.filters

import android.widget.EditText
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.mockSuccessfulCardConfiguration
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PanLengthFilterTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val pan = EditText(context)

    @Before
    fun setup() {
        mockSuccessfulCardConfiguration()
        pan.filters += PanLengthFilter(true)
    }

    @Test
    fun `should limit to max length`() {
        pan.setText("12345678901234567890")
        assertEquals("1234567890123456789", pan.text.toString())
    }

    @Test
    fun `should allow text within limit`() {
        pan.setText("1")
        assertEquals("1", pan.text.toString())

        pan.setText("1234567890123456789")
        assertEquals("1234567890123456789", pan.text.toString())
    }

    @Test
    fun `should change the max length depending on the pan detected`() {
        // visa
        pan.setText("4112345678901234567890")
        assertEquals("4112345678901234567", pan.text.toString())

        // mastercard
        pan.setText("529212345678901234567890")
        assertEquals("5292123456789012", pan.text.toString())
    }

    @Test
    fun `should allow for spaces in length when formatting is enabled - 19 digits`() {
        val pan = EditText(context)
        pan.filters += PanLengthFilter(false)

        pan.setText("4111 1111 1111 1111 111")
        assertEquals("4111 1111 1111 1111 111", pan.text.toString())
    }

    @Test
    fun `should allow for only 2 spaces in length when formatting is enabled - amex brand`() {
        val pan = EditText(context)
        pan.filters += PanLengthFilter(false)

        pan.setText("3427 93178 931249")
        assertEquals("3427 93178 931249", pan.text.toString())
    }

}
