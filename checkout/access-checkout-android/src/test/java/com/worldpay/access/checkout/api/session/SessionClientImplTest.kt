package com.worldpay.access.checkout.api.session

import com.worldpay.access.checkout.BuildConfig
import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.net.URL

@RunWith(MockitoJUnitRunner::class)
class SessionClientImplTest {

    @InjectMocks
    private lateinit var sessionClient: SessionClientImpl

    @Mock
    private lateinit var httpClient: HttpClient

    @Mock
    private lateinit var sessionDeserializer: Deserializer<SessionResponse>

    @Mock
    private lateinit var sessionSerializer: Serializer<CardSessionRequest>

    private val sessionRequest =
        CardSessionRequest(
            "1111222233334444",
            CardSessionRequest.CardExpiryDate(
                12,
                2020
            ),
            "123",
            "MERCHANT-123"
        )

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun givenRequestToSend_ThenShouldMakeHttpPostWithHeaders() {
        val sessionsUrl = URL("http://localhost")
        val sessionResponse = mock(SessionResponse::class.java)

        given(
            httpClient.doPost(
                sessionsUrl,
                sessionRequest,
                hashMapOf(
                    Pair("Content-Type", "application/vnd.worldpay.verified-tokens-v1.hal+json"),
                    Pair("Accept", "application/vnd.worldpay.verified-tokens-v1.hal+json"),
                    Pair("X-WP-SDK", "access-checkout-android/"+BuildConfig.VERSION_NAME)
                ),
                sessionSerializer,
                sessionDeserializer
            )
        ).willReturn(
            sessionResponse
        )

        val actualResponse = sessionClient.getSessionResponse(sessionsUrl, sessionRequest)

        assertEquals(sessionResponse, actualResponse)
    }

}