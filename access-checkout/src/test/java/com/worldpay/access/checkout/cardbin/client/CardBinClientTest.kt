package com.worldpay.access.checkout.cardbin.client

import com.worldpay.access.checkout.BaseCoroutineTest
import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.URLFactory
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryClient
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.discovery.DiscoveryCache
import com.worldpay.access.checkout.cardbin.api.client.CardBinCacheManager
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient.Companion.WP_API_VERSION
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient.Companion.WP_API_VERSION_VALUE
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient.Companion.WP_CALLER_ID
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient.Companion.WP_CALLER_ID_VALUE
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient.Companion.WP_CONTENT_TYPE
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient.Companion.WP_CONTENT_TYPE_VALUE
import com.worldpay.access.checkout.cardbin.api.request.CardBinRequest
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import com.worldpay.access.checkout.cardbin.api.serialization.CardBinRequestSerializer
import com.worldpay.access.checkout.cardbin.api.serialization.CardBinResponseDeserializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.api.exception.ClientErrorException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.net.URL
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CardBinClientTest : BaseCoroutineTest() {

    private val baseUrl = URL("https://some-base-url")
    private val cardBinEndpoint = "public/card/bindetails"
    private val cardBinUrl = URL("$baseUrl/$cardBinEndpoint")
    private val headers = hashMapOf(
        WP_API_VERSION to WP_API_VERSION_VALUE,
        WP_CALLER_ID to WP_CALLER_ID_VALUE,
        WP_CONTENT_TYPE to WP_CONTENT_TYPE_VALUE
    )

    private lateinit var httpsClient: HttpsClient
    private lateinit var urlFactory: URLFactory
    private lateinit var serializer: CardBinRequestSerializer
    private lateinit var deserializer: CardBinResponseDeserializer
    private lateinit var cardBinRequest: CardBinRequest
    private lateinit var cacheManager: CardBinCacheManager

    @Before
    fun setUp() {
        httpsClient = mock(HttpsClient::class.java)
        urlFactory = mock(URLFactory::class.java)
        serializer = mock(CardBinRequestSerializer::class.java)
        deserializer = mock(CardBinResponseDeserializer::class.java)
        cardBinRequest = CardBinRequest("1111222233334444", "some-id")

        // This is used to set up the behaviour of the service discovery so that the
        // HttpsClient mock can use that URL to perform calls
        val cacheKey = DiscoverLinks.cardBinDetails.endpoints[0].key
        DiscoveryCache.results[cacheKey] = cardBinUrl

        ApiDiscoveryClient.initialise("http://localhost")
    }

    @Test
    fun `should construct CardBinClient with minimum required args`() = runTest {
        //Added this tests to cover default arguments
        val client = CardBinClient()

        assertNotNull(client)
    }

    @Test
    fun `should make expected http request and return response`() = runTest {
        val client = createCardBinClient()

        val cardBinResponse = mock(CardBinResponse::class.java)
        given(httpsClient.doPost(cardBinUrl, cardBinRequest, headers, serializer, deserializer))
            .willReturn(cardBinResponse)

        val actualResponse = client.fetchCardBinResponseWithRetry(cardBinRequest)
        assertEquals(cardBinResponse, actualResponse)
    }

    @Test
    fun `should use cached response on subsequent calls with same pan number`() = runTest {

        val client = createCardBinClient()

        val cardBinResponse = mock(CardBinResponse::class.java)
        given(httpsClient.doPost(cardBinUrl, cardBinRequest, headers, serializer, deserializer))
            .willReturn(cardBinResponse)

        val firstCall = client.fetchCardBinResponseWithRetry(cardBinRequest)
        assertEquals(cardBinResponse, firstCall)

        val secondCall = client.fetchCardBinResponseWithRetry(cardBinRequest)
        //Response should match
        assertEquals(cardBinResponse, secondCall)

        //HTTP call was only issued once because of cache
        verify(httpsClient, times(1)).doPost(any(), any(), any(), any(), any())
    }

    @Test
    fun `should return result on first attempt`() =
        runTest {
            val client = createCardBinClient()

            val cardBinResponse = mock(CardBinResponse::class.java)
            given(httpsClient.doPost(cardBinUrl, cardBinRequest, headers, serializer, deserializer))
                .willReturn(cardBinResponse)

            val actualResponse = client.fetchCardBinResponseWithRetry(cardBinRequest)

            assertEquals(cardBinResponse, actualResponse)
        }

    @Test
    fun `should retry and succeed on second attempt`() =
        runTest {
            val client = createCardBinClient()

            val cardBinResponse = mock(CardBinResponse::class.java)
            given(httpsClient.doPost(cardBinUrl, cardBinRequest, headers, serializer, deserializer))
                .willThrow(RuntimeException("First attempt failed"))
                .willReturn(cardBinResponse)

            val actualResponse = client.fetchCardBinResponseWithRetry(cardBinRequest)

            assertEquals(cardBinResponse, actualResponse)
        }

    @Test
    fun `should throw exception after max attempts reached`() =
        runTest {
            val client = createCardBinClient()

            given(httpsClient.doPost(cardBinUrl, cardBinRequest, headers, serializer, deserializer))
                .willThrow(RuntimeException("Request failed"))

            val ex = runCatching {
                client.fetchCardBinResponseWithRetry(cardBinRequest)
            }.exceptionOrNull()

            assertTrue(ex is AccessCheckoutException)
            assertEquals("Failed after 3 attempts", ex?.message)
        }

    @Test
    fun `should throw exception if response is a client server error (code 400 to 499) and not retry`() =
        runTest {
            val client = createCardBinClient()

            given(httpsClient.doPost(cardBinUrl, cardBinRequest, headers, serializer, deserializer))
                .willThrow(
                    AccessCheckoutException(
                        "HTTP response code: 400",
                        ClientErrorException(errorCode = 400)
                    )
                )

            val ex = runCatching {
                client.fetchCardBinResponseWithRetry(cardBinRequest)
            }.exceptionOrNull()

            assertTrue(ex is AccessCheckoutException)
            assertTrue(ex?.cause is ClientErrorException)
            assertEquals("HTTP response code: 400", ex?.message)
            assertNotEquals("Failed after 3 attempts", ex?.message)
        }

    @Test
    fun `should re throw CancellationException if CancellationException occurs`() =
        runTest {
            val client = createCardBinClient()

            given(httpsClient.doPost(cardBinUrl, cardBinRequest, headers, serializer, deserializer))
                .willThrow(
                    CancellationException(
                        "cancelled co-routine",
                    )
                )

            val ex = runCatching {
                client.fetchCardBinResponseWithRetry(cardBinRequest)
            }.exceptionOrNull()

            assertTrue(ex is CancellationException)
        }


    private fun createCardBinClient(): CardBinClient {
        cacheManager = CardBinCacheManager()
        return CardBinClient(
            httpsClient = httpsClient,
            deserializer = deserializer,
            serializer = serializer,
            cacheManager = cacheManager
        )
    }
}
