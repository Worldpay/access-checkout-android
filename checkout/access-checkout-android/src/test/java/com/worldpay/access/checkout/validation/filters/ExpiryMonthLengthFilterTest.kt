package com.worldpay.access.checkout.validation.filters

import android.widget.EditText
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class ExpiryMonthLengthFilterTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val month = EditText(context)

    @Before
    fun setup() {
        month.filters += ExpiryMonthLengthFilter(CARD_CONFIG_BASIC)
    }

    @Test
    fun `should limit to max length`() {
        month.setText("123")
        assertEquals("12", month.text.toString())
    }

    @Test
    fun `should allow text within limit`() {
        month.setText("1")
        assertEquals("1", month.text.toString())

        month.setText("12")
        assertEquals("12", month.text.toString())
    }

}
