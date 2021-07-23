package com.worldpay.access.checkout.client.validation.pan

import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PanFilteringIntegrationTest : AbstractValidationIntegrationTest() {

    @Before
    fun setup() {
        initialiseValidation(enablePanFormatting = true)
    }

    @Test
    fun `should trim and move the cursor to end of pan when pasting over existing pan entirely`() {
        pan.setText("1234 5678 90")
        pan.setText("4444333322221111000099998888")

        assertEquals("4444 3333 2222 1111 000", pan.text.toString())
        assertEquals(23, pan.selectionEnd)
    }

    @Test
    fun `should strip out non digits and take max number of digits allowed by brand`() {
        pan.setText("1234 5678 90")
        pan.setText("4444abc3333def2222ghi1111klm0000nop9999")

        assertEquals("4444 3333 2222 1111 000", pan.text.toString())
        assertEquals(23, pan.selectionEnd)
    }

}
