package com.worldpay.access.checkout.validation.filters

import android.widget.EditText
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.mockSuccessfulCardConfiguration
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation

@RunWith(RobolectricTestRunner::class)
class PanLengthFilterTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val pan = EditText(context)

    @Before
    fun setup() {
        mockSuccessfulCardConfiguration()
        pan.filters += PanLengthFilter(false)
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
        pan.filters += PanLengthFilter(true)

        pan.setText("4111 1111 1111 1111 111")
        assertEquals("4111 1111 1111 1111 111", pan.text.toString())
    }

    @Test
    fun `should allow for only 2 spaces in length when formatting is enabled - amex brand`() {
        val pan = EditText(context)
        pan.filters += PanLengthFilter(true)

        pan.setText("3427 931789 31249")
        assertEquals("3427 931789 31249", pan.text.toString())
    }

    @Test
    fun `should strip out non digits before applying max length on pan - formatting enabled`() {
        val pan = EditText(context)
        val panLengthFilter = PanLengthFilter(true)
        pan.filters += panLengthFilter
        val maxLength = panLengthFilter.getMaxLength("888abc888abc888abc888abc888abc888abc888abc")

        pan.setText("888abc888abc888abc888abc888abc888abc888abc")

        assertEquals(23, maxLength)
        assertEquals("8888888888888888888", pan.text.toString())
    }

    @Test
    fun `should strip out non digits before applying max length on pan - formatting disabled`() {
        val pan = EditText(context)
        val panLengthFilter = PanLengthFilter(false)
        pan.filters += panLengthFilter
        val maxLength = panLengthFilter.getMaxLength("888abc888abc888abc888abc888abc888abc888abc")

        pan.setText("888abc888abc888abc888abc888abc888abc888abc")

        assertEquals(19, maxLength)
        assertEquals("8888888888888888888", pan.text.toString())
    }
}
