package com.worldpay.access.checkout.session.api.client

import com.worldpay.access.checkout.BuildConfig
import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.session.api.request.CvcSessionRequest
import com.worldpay.access.checkout.session.api.response.SessionResponse
import java.net.URL
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CvcSessionClientTest {

    @InjectMocks
    private lateinit var sessionClient: CvcSessionClient

    @Mock
    private lateinit var httpsClient: HttpsClient

    @Mock
    private lateinit var deserializer: Deserializer<SessionResponse>

    @Mock
    private lateinit var serializer: Serializer<CvcSessionRequest>

    @Test
    fun `should make expected http request when getting session response`() {
        val url = URL("https://localhost:8443")
        val sessionResponse = mock(SessionResponse::class.java)

        val headers = hashMapOf(
            Pair("Content-Type", SESSIONS_MEDIA_TYPE),
            Pair("Accept", SESSIONS_MEDIA_TYPE),
            Pair("X-WP-SDK", "access-checkout-android/" + BuildConfig.VERSION_NAME)
        )

        val sessionRequest =
            CvcSessionRequest(
                cvc = "123",
                identity = "merchant-123"
            )

        given(httpsClient.doPost(url, sessionRequest, headers, serializer, deserializer))
            .willReturn(sessionResponse)

        val actualResponse = sessionClient.getSessionResponse(url, sessionRequest)

        assertEquals(sessionResponse, actualResponse)
    }
}
