package com.worldpay.access.checkout.validation.cardbin.api

import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.testutils.PlainRobolectricTestRunner
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(PlainRobolectricTestRunner::class)
class CardBinRequestSerializerTest {

    private val cardBinSerializer: Serializer<CardBinRequest> = CardBinRequestSerializer()

    @Test
    fun `should be able to serialize card bin api request to json`() {
        val cardBinRequest =
            CardBinRequest(
                cardNumber = "444433332222",
                checkoutId = "checkout"
            )

        val expectedRequest = """
                {
                    "cardNumber": "444433332222",
                    "checkoutId": "checkout"
                }"""

        val serializedJsonRequest = cardBinSerializer.serialize(cardBinRequest)

        assertEquals(removeWhitespace(expectedRequest), removeWhitespace(serializedJsonRequest))
    }

    private fun removeWhitespace(string: String): String {
        return string.replace("\\s".toRegex(), "")
    }
}
