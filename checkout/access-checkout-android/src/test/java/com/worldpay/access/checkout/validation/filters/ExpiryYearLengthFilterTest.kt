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
class ExpiryYearLengthFilterTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val year = EditText(context)

    @Before
    fun setup() {
        year.filters += ExpiryYearLengthFilter(CARD_CONFIG_BASIC)
    }

    @Test
    fun `should limit to max length`() {
        year.setText("123")
        assertEquals("12", year.text.toString())
    }

    @Test
    fun `should allow text within limit`() {
        year.setText("1")
        assertEquals("1", year.text.toString())

        year.setText("12")
        assertEquals("12", year.text.toString())
    }

}
