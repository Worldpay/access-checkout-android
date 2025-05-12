package com.worldpay.access.checkout.session.api.serialization

import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.request.CardSessionRequest.CardExpiryDate
import com.worldpay.access.checkout.session.api.request.CvcSessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequest
import kotlin.test.assertFailsWith
import org.junit.Assert.assertEquals
import org.junit.Test

class CvcSessionRequestSerializerTest {

    private val sessionRequestSerializer: Serializer<SessionRequest> = CvcSessionRequestSerializer()

    @Test
    fun `should be able to serialize cvc session request to json`() {
        val sessionRequest =
            CvcSessionRequest(
                cvc = "123",
                identity = "MERCHANT-123"
            )

        val expectedRequest = """
                {
                    "cvc": "123",
                    "identity": "MERCHANT-123"
                }"""

        val serializedJsonRequest = sessionRequestSerializer.serialize(sessionRequest)

        assertEquals(removeWhitespace(expectedRequest), removeWhitespace(serializedJsonRequest))
    }

    @Test
    fun `should fail when trying to serialise a cvc session request`() {
        val sessionRequest =
            CardSessionRequest(
                cardNumber = "0000111122223333",
                cardExpiryDate = CardExpiryDate(12, 2020),
                cvc = "123",
                identity = "MERCHANT-123"
            )

        assertFailsWith<AccessCheckoutException> {
            sessionRequestSerializer.serialize(
                sessionRequest
            )
        }
    }

    private fun removeWhitespace(string: String): String {
        return string.replace("\\s".toRegex(), "")
    }
}
