package com.worldpay.access.checkout.cardbin.client

import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.URLFactory
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient
import com.worldpay.access.checkout.cardbin.api.client.WP_API_VERSION
import com.worldpay.access.checkout.cardbin.api.client.WP_API_VERSION_VALUE
import com.worldpay.access.checkout.cardbin.api.client.WP_CALLER_ID
import com.worldpay.access.checkout.cardbin.api.client.WP_CALLER_ID_VALUE
import com.worldpay.access.checkout.cardbin.api.client.WP_CONTENT_TYPE
import com.worldpay.access.checkout.cardbin.api.client.WP_CONTENT_TYPE_VALUE
import com.worldpay.access.checkout.cardbin.api.request.CardBinRequest
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import com.worldpay.access.checkout.cardbin.api.serialization.CardBinRequestSerializer
import com.worldpay.access.checkout.cardbin.api.serialization.CardBinResponseDeserializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.net.URL
import kotlinx.coroutines.test.runBlockingTest as runAsBlockingTest

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CardBinClientTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val baseUrl = "https://some-base-url"
    private val cardBinEndpoint = "public/card/bindetails"
    private val cardBinUrl = URL("$baseUrl/$cardBinEndpoint")
    private val headers = hashMapOf(
        Pair(WP_API_VERSION, WP_API_VERSION_VALUE),
        Pair(WP_CALLER_ID, WP_CALLER_ID_VALUE),
        Pair(WP_CONTENT_TYPE, WP_CONTENT_TYPE_VALUE)
    )

    @Test
    fun `should make expected http request when calling the card bin service`() =
        runAsBlockingTest {
            val cardBinResponse = mock<CardBinResponse>()
            val httpsClient = mock<HttpsClient>()
            val urlFactory = mock<URLFactory>()
            val serializer = mock<CardBinRequestSerializer>()
            val deserializer = mock<CardBinResponseDeserializer>()

            val cardBinRequest =
                CardBinRequest(
                    cardNumber = "1111222233334444",
                    checkoutId = "some-id"
                )


            given(urlFactory.getURL("$baseUrl/$cardBinEndpoint")).willReturn(cardBinUrl)
            given(httpsClient.doPost(cardBinUrl, cardBinRequest, headers, serializer, deserializer))
                .willReturn(cardBinResponse)

            val cardBinClient =
                CardBinClient(baseUrl, httpsClient, deserializer, serializer)

            val actualResponse = cardBinClient.getCardBinResponse(cardBinRequest)

            assertEquals(cardBinResponse, actualResponse)
        }

    @Test
    fun `should not swallow exception thrown by HttpClient`() =
        runAsBlockingTest {
            val httpsClient = mock<HttpsClient>()
            val urlFactory = mock<URLFactory>()
            val serializer = mock<CardBinRequestSerializer>()
            val deserializer = mock<CardBinResponseDeserializer>()

            val cardBinRequest =
                CardBinRequest(
                    cardNumber = "1111222233334444",
                    checkoutId = "some-id"
                )

            given(urlFactory.getURL("$baseUrl/$cardBinEndpoint")).willReturn(cardBinUrl)
            given(httpsClient.doPost(cardBinUrl, cardBinRequest, headers, serializer, deserializer))
                .willThrow(AccessCheckoutException("Access Checkout Exception"))

            val cardBinClient =
                CardBinClient(baseUrl, httpsClient, deserializer, serializer)

            val result = runCatching {
                cardBinClient.getCardBinResponse(cardBinRequest)
            }

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is AccessCheckoutException)
            assertEquals("Access Checkout Exception", result.exceptionOrNull()?.message)
        }
}
