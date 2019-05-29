package com.worldpay.access.checkout.api.serialization

import com.worldpay.access.checkout.api.SessionRequest
import com.worldpay.access.checkout.api.SessionRequest.CardExpiryDate
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SessionRequestSerializerTest {

    private val sessionRequestSerializer: Serializer<SessionRequest> = SessionRequestSerializer()

    @Test
    fun givenSessionRequestInstanceThenShouldSerializeToJson() {
        val sessionRequest = SessionRequest("0000111122223333", CardExpiryDate(12, 2020), "123", "MERCHANT-123")
        val expectedRequest = """
                {
                    "cardNumber": "0000111122223333",
                    "cardExpiryDate": {
                        "month": 12,
                        "year": 2020
                    },
                    "cvc": "123",
                    "identity": "MERCHANT-123"
                }"""

        val serializedJsonRequest = sessionRequestSerializer.serialize(sessionRequest)

        assertEquals(removeWhitespace(expectedRequest), removeWhitespace(serializedJsonRequest))
    }


    private fun removeWhitespace(string: String): String {
        return string.replace("\\s".toRegex(), "")
    }
}