package com.worldpay.access.checkout.session.api.client

import com.worldpay.access.checkout.BuildConfig
import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.response.SessionResponse
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.net.URL

@RunWith(MockitoJUnitRunner::class)
class VerifiedTokenSessionClientTest {

    @InjectMocks
    private lateinit var sessionClient: VerifiedTokenSessionClient

    @Mock
    private lateinit var httpClient: HttpClient

    @Mock
    private lateinit var deserializer: Deserializer<SessionResponse>

    @Mock
    private lateinit var serializer: Serializer<CardSessionRequest>

    @Test
    fun `should make expected http request when getting session response`() {
        val url = URL("http://localhost")
        val sessionResponse = mock(SessionResponse::class.java)

        val headers = hashMapOf(
            Pair("Content-Type", "application/vnd.worldpay.verified-tokens-v1.hal+json"),
            Pair("Accept", "application/vnd.worldpay.verified-tokens-v1.hal+json"),
            Pair("X-WP-SDK", "access-checkout/" + BuildConfig.VERSION_NAME)
        )

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

        given(httpClient.doPost(url, sessionRequest, headers, serializer, deserializer))
            .willReturn(sessionResponse)

        val actualResponse = sessionClient.getSessionResponse(url, sessionRequest)

        assertEquals(sessionResponse, actualResponse)
    }

}
