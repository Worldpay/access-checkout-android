package com.worldpay.access.checkout.api.session.serialization

import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.api.session.CVVSessionRequest
import com.worldpay.access.checkout.api.session.CardSessionRequest
import com.worldpay.access.checkout.api.session.CardSessionRequest.CardExpiryDate
import com.worldpay.access.checkout.api.session.SessionRequest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertFailsWith

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CardSessionRequestSerializerTest {

    private val sessionRequestSerializer: Serializer<SessionRequest> = CardSessionRequestSerializer()

    @Test
    fun `should be able to serialize card session request to json`() {
        val sessionRequest = CardSessionRequest(
            cardNumber = "0000111122223333",
            cardExpiryDate = CardExpiryDate(12, 2020),
            cvv = "123",
            identity = "MERCHANT-123"
        )

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

    @Test
    fun `should fail when trying to serialise a cvv session request`() {
        val sessionRequest = CVVSessionRequest("123", "MERCHANT-123")
        assertFailsWith<IllegalArgumentException> { sessionRequestSerializer.serialize(sessionRequest) }
    }

    private fun removeWhitespace(string: String): String {
        return string.replace("\\s".toRegex(), "")
    }
}