package com.worldpay.access.checkout.session.api.client

import com.worldpay.access.checkout.BuildConfig
import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.response.SessionResponse
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import java.net.URL
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest as runAsBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CardSessionClientTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @InjectMocks
    private lateinit var sessionClient: CardSessionClient

    @Mock
    private lateinit var httpsClient: HttpsClient

    @Mock
    private lateinit var deserializer: Deserializer<SessionResponse>

    @Mock
    private lateinit var serializer: Serializer<CardSessionRequest>

    private val url = URL("https://some-url.com")

    @Test
    fun `should make expected http request when getting session response`() = runAsBlockingTest {
        val sessionResponse = mock(SessionResponse::class.java)

        val headers = hashMapOf(
            Pair("Content-Type", VERIFIED_TOKENS_MEDIA_TYPE),
            Pair("Accept", VERIFIED_TOKENS_MEDIA_TYPE),
            Pair("X-WP-SDK", "access-checkout-android/" + BuildConfig.VERSION_NAME)
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

        given(httpsClient.doPost(url, sessionRequest, headers, serializer, deserializer))
            .willReturn(sessionResponse)

        val actualResponse = sessionClient.getSessionResponse(url, sessionRequest)

        assertEquals(sessionResponse, actualResponse)
    }
}
