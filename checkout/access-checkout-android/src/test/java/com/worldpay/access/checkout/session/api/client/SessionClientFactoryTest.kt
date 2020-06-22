package com.worldpay.access.checkout.session.api.client

import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.request.CvcSessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequest
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SessionClientFactoryTest {

    private val sessionClientFactory: SessionClientFactory = SessionClientFactory()

    @Test
    fun `should be able to create a card session client`() {
        val sessionRequest =
            CardSessionRequest(
                cardNumber = "1111222233334444",
                cardExpiryDate = CardSessionRequest.CardExpiryDate(
                    12,
                    2020
                ),
                cvc = "123",
                identity = "MERCHANT-123"
            )

        val sessionClient = sessionClientFactory.createClient(sessionRequest)

        assertNotNull(sessionClient)
        assertTrue { sessionClient is VerifiedTokenSessionClient }
    }

    @Test
    fun `should be able to create a cvc session client`() {
        val sessionRequest =
            CvcSessionRequest(
                cvc = "123",
                identity = "merchant-123"
            )

        val sessionClient = sessionClientFactory.createClient(sessionRequest)

        assertNotNull(sessionClient)
        assertTrue { sessionClient is PaymentsCvcSessionClient }
    }

    @Test
    fun `should throw exception when suitable request is not found`() {
        assertFailsWith<IllegalArgumentException> {
            sessionClientFactory.createClient(InvalidSessionRequest())
        }
    }

    class InvalidSessionRequest :
        SessionRequest

}
