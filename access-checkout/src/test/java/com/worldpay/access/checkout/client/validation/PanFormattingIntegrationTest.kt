package com.worldpay.access.checkout.client.validation

import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PanFormattingIntegrationTest : AbstractValidationIntegrationTest() {

    @Test
    fun `should be formatting pan with a space between every 4 digits - visa`() {
        initialiseWithoutAcceptedCardBrands(enablePanFormatting = true)

        pan.setText("4111111111111111")

        assertEquals("4111 1111 1111 1111", pan.text.toString())
    }

    @Test
    fun `should be formatting pan with a space between after 4, 6 and 5 digits`() {
        initialiseWithoutAcceptedCardBrands(enablePanFormatting = true)

        pan.setText("342793178931249")

        assertEquals("3427 931789 31249", pan.text.toString())
    }

    @Test
    fun `should not be formatting pan when formatting is disabled`() {
        initialiseWithoutAcceptedCardBrands(enablePanFormatting = false)

        pan.setText("4111111111111111")

        assertEquals("4111111111111111", pan.text.toString())
    }
}
