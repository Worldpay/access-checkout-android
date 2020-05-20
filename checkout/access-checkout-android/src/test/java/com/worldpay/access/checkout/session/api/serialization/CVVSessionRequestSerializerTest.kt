package com.worldpay.access.checkout.session.api.serialization

import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.session.api.request.CVVSessionRequest
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.request.CardSessionRequest.CardExpiryDate
import com.worldpay.access.checkout.session.api.request.SessionRequest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertFailsWith

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CVVSessionRequestSerializerTest {

    private val sessionRequestSerializer: Serializer<SessionRequest> = CVVSessionRequestSerializer()

    @Test
    fun `should be able to serialize cvv session request to json`() {
        val sessionRequest =
            CVVSessionRequest(
                cvv = "123",
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
    fun `should fail when trying to serialise a cvv session request`() {
        val sessionRequest =
            CardSessionRequest(
                cardNumber = "0000111122223333",
                cardExpiryDate = CardExpiryDate(12, 2020),
                cvv = "123",
                identity = "MERCHANT-123"
            )

        assertFailsWith<IllegalArgumentException> {
            sessionRequestSerializer.serialize(
                sessionRequest
            )
        }
    }

    private fun removeWhitespace(string: String): String {
        return string.replace("\\s".toRegex(), "")
    }
}