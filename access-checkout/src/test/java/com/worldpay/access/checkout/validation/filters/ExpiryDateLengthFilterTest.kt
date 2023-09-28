package com.worldpay.access.checkout.validation.filters

import com.worldpay.access.checkout.ui.AccessEditText
import kotlin.test.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation

@RunWith(RobolectricTestRunner::class)
class ExpiryDateLengthFilterTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val expiryDate = AccessEditText(context)

    @Before
    fun setup() {
        expiryDate.filters += ExpiryDateLengthFilter()
    }

    @Test
    fun `should limit to max length`() {
        expiryDate.setText("02/299")
        assertEquals("02/29", expiryDate.text)
    }

    @Test
    fun `should allow text within limit`() {
        expiryDate.setText("1")
        assertEquals("1", expiryDate.text)

        expiryDate.setText("12")
        assertEquals("12", expiryDate.text)
    }
}
