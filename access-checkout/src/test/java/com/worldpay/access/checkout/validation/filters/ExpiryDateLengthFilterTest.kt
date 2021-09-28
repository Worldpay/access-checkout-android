package com.worldpay.access.checkout.validation.filters

import android.widget.EditText
import com.worldpay.access.checkout.testutils.PlainRobolectricTestRunner
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowInstrumentation

@RunWith(PlainRobolectricTestRunner::class)
class ExpiryDateLengthFilterTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val expiryDate = EditText(context)

    @Before
    fun setup() {
        expiryDate.filters += ExpiryDateLengthFilter()
    }

    @Test
    fun `should limit to max length`() {
        expiryDate.setText("02/299")
        assertEquals("02/29", expiryDate.text.toString())
    }

    @Test
    fun `should allow text within limit`() {
        expiryDate.setText("1")
        assertEquals("1", expiryDate.text.toString())

        expiryDate.setText("12")
        assertEquals("12", expiryDate.text.toString())
    }
}
