package com.worldpay.access.checkout.validation.filters

import android.text.SpannableString
import kotlin.test.assertEquals
import org.junit.Test

class PanNumericFilterTest {

    @Test
    fun `should remove non digits from text`() {
        val filter = PanNumericFilter()
        val result = filter.filter("888abc888", 0, 0, SpannableString(""), 0, 0)

        assertEquals("888888", result)
    }

    @Test
    fun `should keep spaces in text`() {
        val filter = PanNumericFilter()
        val result = filter.filter("888 888", 0, 0, SpannableString(""), 0, 0)

        assertEquals("888 888", result)
    }
}
