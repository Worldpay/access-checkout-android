package com.worldpay.access.checkout.validation.listeners.text

import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.AMEX_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.formatter.PanFormatter
import org.junit.Test
import kotlin.test.assertEquals

class PanFormatterTest {
    
    private val panFormatter = PanFormatter(false)

    @Test
    fun `should not do any formatting when formatting is disabled`() {
        val panFormatter = PanFormatter(true)

        val pan = panFormatter.format(VISA_PAN, VISA_BRAND)

        assertEquals(VISA_PAN, pan)
    }
    
    @Test
    fun `should not reformat a pan that is less than 4 digits`() {
        val pan = panFormatter.format("411", VISA_BRAND)

        assertEquals("411", pan)
    }

    @Test
    fun `should not reformat a pan that is 4 digits`() {
        val pan = panFormatter.format("4111", VISA_BRAND)

        assertEquals("4111", pan)
    }

    @Test
    fun `should reformat a pan that has been incorrectly formatted`() {
        val pan = panFormatter.format("515012 039284", VISA_BRAND)

        assertEquals("5150 1203 9284", pan)
    }

    @Test
    fun `should reformat a pan that has been incorrectly formatted in too small groups`() {
        val pan = panFormatter.format("5151 012 039", VISA_BRAND)

        assertEquals("5151 0120 39", pan)
    }

    @Test
    fun `should reformat an amex pan that has been incorrectly formatted - too small groups`() {
        val pan = panFormatter.format("5151 012 039", AMEX_BRAND)

        assertEquals("5151 012039", pan)
    }

    @Test
    fun `should reformat an amex pan that has been incorrectly formatted - too short middle group`() {
        val pan = panFormatter.format("5151 01239 33333", AMEX_BRAND)

        assertEquals("5151 012393 3333", pan)
    }

    @Test
    fun `should reformat an amex pan that has been incorrectly formatted - too many groups`() {
        val pan = panFormatter.format("5151 012393 333 33", AMEX_BRAND)

        assertEquals("5151 012393 33333", pan)
    }

    @Test
    fun `should not reformat an amex pan that has correctly formatted - two groups`() {
        val pan = panFormatter.format("5151 012393", AMEX_BRAND)

        assertEquals("5151 012393", pan)
    }

    @Test
    fun `should not reformat an amex pan that has correctly formatted - three groups`() {
        val pan = panFormatter.format("5151 012393 4", AMEX_BRAND)

        assertEquals("5151 012393 4", pan)
    }

    @Test
    fun `should not reformat an amex pan that has correctly formatted - less than 10 digits`() {
        val pan = panFormatter.format("5151 01239", AMEX_BRAND)

        assertEquals("5151 01239", pan)
    }

    @Test
    fun `should reformat a pan that has 5 digits`() {
        val pan = panFormatter.format("51501", VISA_BRAND)

        assertEquals("5150 1", pan)
    }

    @Test
    fun `should reformat a pan that has 12 digits`() {
        val pan = panFormatter.format("515012039284", VISA_BRAND)

        assertEquals("5150 1203 9284", pan)
    }

    @Test
    fun `should reformat a pan that has 13 digits`() {
        val pan = panFormatter.format("5150120392842", VISA_BRAND)

        assertEquals("5150 1203 9284 2", pan)
    }

    @Test
    fun `should reformat a pan that has 14 digits`() {
        val pan = panFormatter.format("51501203928423", VISA_BRAND)

        assertEquals("5150 1203 9284 23", pan)
    }

    @Test
    fun `should reformat a pan that has 15 digits`() {
        val pan = panFormatter.format("515012039284232", VISA_BRAND)

        assertEquals("5150 1203 9284 232", pan)
    }

    @Test
    fun `should reformat a pan that has 16 digits`() {
        val pan = panFormatter.format("5150120392842323", VISA_BRAND)

        assertEquals("5150 1203 9284 2323", pan)
    }

    @Test
    fun `should reformat a pan that has 17 digits`() {
        val pan = panFormatter.format("51501203928423234", VISA_BRAND)

        assertEquals("5150 1203 9284 2323 4", pan)
    }

    @Test
    fun `should reformat a pan that has 18 digits`() {
        val pan = panFormatter.format("515012039284232342", VISA_BRAND)

        assertEquals("5150 1203 9284 2323 42", pan)
    }

    @Test
    fun `should reformat a pan that has 19 digits`() {
        val pan = panFormatter.format("5150120392842323423", VISA_BRAND)

        assertEquals("5150 1203 9284 2323 423", pan)
    }

    @Test
    fun `should reformat an amex pan by 4,6,5 groups`() {
        val pan = panFormatter.format(AMEX_PAN, AMEX_BRAND)

        assertEquals("3427 931789 31249", pan)
    }

    @Test
    fun `should reformat an amex pan by 4,6 group - 10 digits`() {
        val pan = panFormatter.format("3427931789", AMEX_BRAND)

        assertEquals("3427 931789", pan)
    }

    @Test
    fun `should reformat an amex pan by 4,6,5 group when formatted incorrectly - incorrect on 2nd part`() {
        val pan = panFormatter.format("3427 9317893", AMEX_BRAND)

        assertEquals("3427 931789 3", pan)
    }

    @Test
    fun `should reformat an amex pan by 4,6,5 group when formatted incorrectly - incorrect on 3rd part`() {
        val pan = panFormatter.format("3427 9317 931249", AMEX_BRAND)

        assertEquals("3427 931793 1249", pan)
    }

    @Test
    fun `should not reformat an amex pan when only 4 digits entered`() {
        val pan = panFormatter.format("3427", AMEX_BRAND)

        assertEquals("3427", pan)
    }

    @Test
    fun `should not reformat an amex pan when only 2 digits entered`() {
        val pan = panFormatter.format("34", AMEX_BRAND)

        assertEquals("34", pan)
    }

    @Test
    fun `should not reformat an amex pan when 0 digits entered`() {
        val pan = panFormatter.format("", AMEX_BRAND)

        assertEquals("", pan)
    }
}
