package com.worldpay.access.checkout.api.session.client

import com.worldpay.access.checkout.api.session.CVVSessionRequest
import com.worldpay.access.checkout.api.session.CardSessionRequest
import com.worldpay.access.checkout.api.session.SessionRequest
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SessionClientFactoryTest {

    private val sessionClientFactory: SessionClientFactory = SessionClientFactory()

    @Test
    fun `should be able to create a card session client`() {
        val sessionRequest = CardSessionRequest(
            cardNumber = "1111222233334444",
            cardExpiryDate = CardSessionRequest.CardExpiryDate(12, 2020),
            cvv = "123",
            identity = "MERCHANT-123"
        )

        val sessionClient = sessionClientFactory.createClient(sessionRequest)

        assertNotNull(sessionClient)
        assertTrue { sessionClient is CardSessionClient }
    }

    @Test
    fun `should be able to create a cvv session client`() {
        val sessionRequest = CVVSessionRequest(
            cvv = "123",
            identity = "merchant-123"
        )

        val sessionClient = sessionClientFactory.createClient(sessionRequest)

        assertNotNull(sessionClient)
        assertTrue { sessionClient is CVVSessionClient }
    }

    @Test
    fun `should throw exception when suitable request is not found`() {
        assertFailsWith<IllegalArgumentException> {
            sessionClientFactory.createClient(InvalidSessionRequest())
        }
    }

    class InvalidSessionRequest : SessionRequest

}
